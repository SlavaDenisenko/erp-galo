package com.denisenko.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "supplier-service")
public interface SupplierConfig {
    String url();

    String receiveSupplyPath();
}
