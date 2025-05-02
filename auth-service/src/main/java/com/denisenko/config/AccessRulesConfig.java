package com.denisenko.config;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "access")
public interface AccessRulesConfig {
    List<Rule> rules();

    interface Rule {
        String path();

        String method();

        List<String> roles();
    }
}
