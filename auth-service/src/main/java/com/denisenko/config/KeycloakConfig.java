package com.denisenko.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "keycloak")
public interface KeycloakConfig {

    String url();

    String clientId();

    String clientSecret();
}
