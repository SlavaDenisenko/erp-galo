package com.denisenko.reportservice.service;

import com.denisenko.reportservice.dto.*;
import com.denisenko.reportservice.model.*;
import com.denisenko.reportservice.repository.ReportTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final RedisCacheService redisCacheService;
    private final ReportTemplateRepository reportTemplateRepository;
    private final ReportValidator reportValidator;
    private final ReportCalculator reportCalculator;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:report:";

    public ReportTemplateDto createTemplate(ReportTemplateDto reportTemplateDto, String idempotencyKey) {
        ReportTemplateDto exists = redisCacheService.get(IDEMPOTENCY_PREFIX + idempotencyKey, ReportTemplateDto.class);
        if (exists != null) return exists;

        ReportTemplate reportTemplate = mapToEntity(reportTemplateDto);
        reportTemplateRepository.save(reportTemplate);
        ReportTemplateDto saved = mapToDto(reportTemplate);
        redisCacheService.put(IDEMPOTENCY_PREFIX + idempotencyKey, saved);
        return saved;
    }

    @Transactional
    public ReportResponse generateReport(ReportRequest reportRequest, String idempotencyKey) {
        ReportResponse exists = redisCacheService.get(IDEMPOTENCY_PREFIX + idempotencyKey, ReportResponse.class);
        if (exists != null) return exists;

        ReportTemplate reportTemplate = reportTemplateRepository.findById(reportRequest.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Report template with ID = " + reportRequest.getTemplateId() + " not found"));
        reportValidator.validate(reportTemplate, reportRequest.getParameters());
        List<Map<String, Object>> reportData = reportCalculator.calculate(
                reportTemplate,
                reportRequest.getParameters(),
                reportRequest.getStartDate(),
                reportRequest.getEndDate(),
                Sale.class); //- FIXME
        ReportResponse reportResponse = new ReportResponse(reportTemplate.getTemplateName(), reportData);
        redisCacheService.put(IDEMPOTENCY_PREFIX + idempotencyKey, reportResponse);
        return reportResponse;
    }

    public List<ReportTemplateDto> getReportTemplates() {
        return reportTemplateRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    private ReportTemplate mapToEntity(ReportTemplateDto reportTemplateDto) {
        ReportTemplate reportTemplate = ReportTemplate.builder()
                .id(reportTemplateDto.getId())
                .templateName(reportTemplateDto.getName())
                .build();

        if (reportTemplateDto.getFilters() != null) {
            reportTemplate.setFilters(reportTemplateDto.getFilters().stream().map(this::mapFilterToEntity).toList());
        }

        if (reportTemplateDto.getAggregations() != null) {
            reportTemplate.setAggregations(reportTemplateDto.getAggregations().stream().map(this::mapAggregationToEntity).toList());
        }

        return reportTemplate;
    }

    private ReportTemplateDto mapToDto(ReportTemplate reportTemplate) {
        ReportTemplateDto reportTemplateDto = ReportTemplateDto.builder()
                .id(reportTemplate.getId())
                .name(reportTemplate.getTemplateName())
                .build();

        if (reportTemplate.getFilters() != null) {
            reportTemplateDto.setFilters(reportTemplate.getFilters().stream().map(this::mapFilterToDto).toList());
        }

        if (reportTemplate.getAggregations() != null) {
            reportTemplateDto.setAggregations(reportTemplate.getAggregations().stream().map(this::mapAggregationToDto).toList());
        }

        return reportTemplateDto;
    }

    private FilterCriteria mapFilterToEntity(FilterCriteriaDto filterDto) {
        FilterCriteria filterCriteria = new FilterCriteria();
        filterCriteria.setField(filterDto.getField());
        filterCriteria.setOperator(Operator.valueOf(filterDto.getOperator().toUpperCase(Locale.ROOT)));
        filterCriteria.setValue(filterDto.getValue());
        return filterCriteria;
    }

    private FilterCriteriaDto mapFilterToDto(FilterCriteria filter) {
        return FilterCriteriaDto.builder()
                .field(filter.getField())
                .operator(filter.getOperator().name())
                .value(filter.getValue())
                .build();
    }

    private AggregationCriteria mapAggregationToEntity(AggregationCriteriaDto aggregationDto) {
        AggregationCriteria aggregationCriteria = new AggregationCriteria();
        aggregationCriteria.setField(aggregationDto.getField());
        aggregationCriteria.setOperation(Operation.valueOf(aggregationDto.getOperation().toUpperCase(Locale.ROOT)));
        return aggregationCriteria;
    }

    private AggregationCriteriaDto mapAggregationToDto(AggregationCriteria aggregation) {
        return AggregationCriteriaDto.builder()
                .field(aggregation.getField())
                .operation(aggregation.getOperation().name())
                .build();
    }
}
