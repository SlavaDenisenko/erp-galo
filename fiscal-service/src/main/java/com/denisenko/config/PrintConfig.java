package com.denisenko.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "print-service")
public interface PrintConfig {
    String url();

    String fiscalShiftPath();

    String fiscalOrderPath();

    int retryAttempts();

    int retryDelay();
}
