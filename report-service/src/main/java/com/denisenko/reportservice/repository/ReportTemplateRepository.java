package com.denisenko.reportservice.repository;

import com.denisenko.reportservice.model.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Integer> {
}
