package com.denisenko.reportservice.controller;

import com.denisenko.reportservice.dto.ReportRequest;
import com.denisenko.reportservice.dto.ReportResponse;
import com.denisenko.reportservice.dto.ReportTemplateDto;
import com.denisenko.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/templates")
    public ResponseEntity<ReportTemplateDto> createTemplate(@RequestBody ReportTemplateDto reportTemplateDto,
                                                            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(reportService.createTemplate(reportTemplateDto, idempotencyKey), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<ReportResponse> generateReport(@RequestBody ReportRequest reportRequest,
                                                         @RequestHeader("Idempotency-Key") String idempotencyKey) {
        var report = reportService.generateReport(reportRequest, idempotencyKey);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/templates")
    public ResponseEntity<List<ReportTemplateDto>> getReportTemplates() {
        var templates = reportService.getReportTemplates();
        return ResponseEntity.ok(templates);
    }
}
