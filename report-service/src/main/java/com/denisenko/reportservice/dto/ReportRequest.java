package com.denisenko.reportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequest {
    private Integer templateId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> parameters;
}
