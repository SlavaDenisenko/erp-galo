package com.denisenko.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "ofd-service")
public interface OFDConfig {
    String url();

    String openShiftPath();

    String closeShiftPath();

    String closeOrderPath();

    int retryAttempts();

    int retryDelay();
}
