package com.denisenko.reportservice.service;

import com.denisenko.reportservice.exception.InvalidReportRequestException;
import com.denisenko.reportservice.model.FilterCriteria;
import com.denisenko.reportservice.model.ReportTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ReportValidator {

    public void validate(ReportTemplate reportTemplate, Map<String, Object> parameters) {
        if (Objects.isNull(reportTemplate))
            throw new InvalidReportRequestException("Template cannot be null.");

        validateFilters(reportTemplate.getFilters(), parameters);
    }

    private void validateFilters(List<FilterCriteria> filters, Map<String, Object> parameters) {
        if (Objects.isNull(filters) || filters.isEmpty()) return;

        List<String> missingFilters = filters.stream()
                .map(FilterCriteria::getField)
                .filter(field -> Objects.isNull(parameters) || Objects.isNull(parameters.get(field)))
                .toList();

        if (!missingFilters.isEmpty())
            throw new InvalidReportRequestException(
                    "Missing required filters: " + String.join(", ", missingFilters)
            );
    }
}
