package com.denisenko.service;

import com.denisenko.config.ReportConfig;
import com.denisenko.config.ReportConfig.ReportTemplate;
import com.denisenko.model.Order;
import com.denisenko.model.OrderItem;
import com.denisenko.model.Report;
import com.denisenko.repository.RedisRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReportService {
    private static final String REPORT_PREFIX_KEY = "report:";

    @Inject
    ReportConfig reportConfig;

    @Inject
    RedisRepository redisRepository;

    public List<String> getAvailableReports() {
        List<String> availableReports = new ArrayList<>();
        reportConfig.reports().forEach(report -> availableReports.add(report.name()));
        return availableReports;
    }

    public Report getReport(String reportName) {
        Optional<ReportTemplate> reportTemplate = reportConfig.reports().stream().filter(report -> report.name().equals(reportName)).findFirst();
        if (reportTemplate.isEmpty())
            return null;

        Object value = redisRepository.findByKey(REPORT_PREFIX_KEY + reportTemplate.get().name(), Object.class);
        return new Report(reportTemplate.get().name(), reportTemplate.get().description(), value);
    }

    public void updateReports(Order order) {
        for (ReportTemplate reportTemplate : reportConfig.reports()) {
            switch (reportTemplate.operation()) {
                case SUM -> updateSumReport(order, reportTemplate);
                case COUNT -> updateCountReport(order, reportTemplate);
            }
        }
    }

    public void clearReports() {
        for (ReportTemplate report : reportConfig.reports()) {
            redisRepository.delete(REPORT_PREFIX_KEY + report.name());
        }
    }

    private void updateSumReport(Order order, ReportTemplate reportTemplate) {
        double currentSum = redisRepository.findByKey(REPORT_PREFIX_KEY + reportTemplate.name(), Double.class).orElse(0.0);
        double newSum = currentSum + order.getItems().stream()
                .mapToDouble(item -> getFieldValue(item, reportTemplate.field()))
                .sum();
        redisRepository.save(REPORT_PREFIX_KEY + reportTemplate.name(), newSum);
    }

    private void updateCountReport(Order order, ReportTemplate reportTemplate) {
        long currentCount = redisRepository.findByKey(REPORT_PREFIX_KEY + reportTemplate.name(), Long.class).orElse(0L);
        long newCount = currentCount + order.getItems().stream()
                .filter(item -> checkCondition(item, reportTemplate.field()))
                .count();
        redisRepository.save(REPORT_PREFIX_KEY + reportTemplate.name(), newCount);
    }

    private double getFieldValue(OrderItem orderItem, String fieldName) {
        try {
            Field field = OrderItem.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (double) field.get(orderItem);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field value: " + fieldName, e);
        }
    }

    private boolean checkCondition(OrderItem orderItem, String condition) {
        try {
            Field field = OrderItem.class.getDeclaredField(condition);
            field.setAccessible(true);
            return (boolean) field.get(orderItem);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check condition: " + condition, e);
        }
    }
}
