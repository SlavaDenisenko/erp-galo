package com.denisenko.config;

import com.denisenko.model.ReportOperation;
import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "reports")
public interface ReportConfig {
    List<ReportTemplate> reports();

    interface ReportTemplate {
        String name();

        String description();

        String field();

        ReportOperation operation();
    }
}
