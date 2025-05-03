package com.denisenko.supplierservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConfigurationProperties(prefix = "scheduler")
@EnableScheduling
@Getter
@Setter
public class SchedulerConfig {
    private String supplierSchedulerCron;
    private String supplierInitialDelay;
    private String supplierFixedRate;
}
