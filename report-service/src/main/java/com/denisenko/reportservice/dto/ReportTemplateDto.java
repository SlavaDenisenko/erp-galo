package com.denisenko.reportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTemplateDto {
    private Integer id;
    private String name;
    private List<FilterCriteriaDto> filters;
    private List<AggregationCriteriaDto> aggregations;
}
