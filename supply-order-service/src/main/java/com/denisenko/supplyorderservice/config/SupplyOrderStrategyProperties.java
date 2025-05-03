package com.denisenko.supplyorderservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "supply-order")
@Component
@Getter
@Setter
public class SupplyOrderStrategyProperties {
    private String plannerStrategy;
}
