package com.denisenko.userservice.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;

@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfig {
    static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:25.0.0";
    static String realmImportFile = "test-realm-realm.json";
    static String realmName = "test-realm";
    static String clientId = "test-client";
    static String clientSecret = "9uNx6hi8kRf6hAVfbbMiWbgVJ7o8L7k1";
    static String principalAttribute = "preferred_username";

    @Bean
    KeycloakContainer keycloakContainer(DynamicPropertyRegistry registry) {
        var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withRealmImportFile(realmImportFile);
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/" + realmName
        );
        registry.add("jwt.auth.converter.resourceId", () -> clientId);
        registry.add("jwt.auth.converter.principalAttribute", () -> principalAttribute);
        registry.add("keycloak.auth-server-url", keycloak::getAuthServerUrl);
        registry.add("keycloak.realm", () -> realmName);
        registry.add("keycloak.resource", () -> clientId);
        registry.add("keycloak.credentials.secret", () -> clientSecret);
        return keycloak;
    }
}
