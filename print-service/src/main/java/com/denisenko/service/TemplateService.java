package com.denisenko.service;

import com.denisenko.model.Order;
import com.denisenko.model.OrderItem;
import com.denisenko.model.Shift;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class TemplateService {

    @Inject
    @Location("fiscal-receipt")
    Template fiscalReceipt;

    @Inject
    @Location("pre-check")
    Template preCheck;

    @Inject
    Template receipt;

    @Inject
    @Location("shift-receipt")
    Template shiftReceipt;

    public String generateReceipt(Order order, List<OrderItem> items) {
        Map<Integer, List<OrderItem>> itemsByCourse = items.stream()
                .collect(Collectors.groupingBy(OrderItem::getCourse));
        List<CourseData> courses = itemsByCourse.entrySet().stream()
                .map(item -> new CourseData(item.getKey(), item.getValue()))
                .toList();

        TemplateInstance template = receipt
                .data("order", order)
                .data("courses", courses);
        return template.render();
    }

    public String generatePreCheck(Order order) {
        TemplateInstance template = preCheck.data("order", order);
        return template.render();
    }

    public String generateFiscalReceipt(Order order) {
        TemplateInstance template = fiscalReceipt.data("order", order);
        return template.render();
    }

    public String generateShiftReceipt(Shift shift) {
        TemplateInstance template = shiftReceipt
                .data("type", shift.getEndTime() == null ? "Open" : "Close")
                .data("shift", shift);
        return template.render();
    }

    public record CourseData(Integer number, List<OrderItem> items) {
    }
}
