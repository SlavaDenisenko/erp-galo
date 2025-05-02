package com.denisenko;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.HashMap;
import java.util.Map;

public class MockKeycloakTestResource implements QuarkusTestResourceLifecycleManager {
    private static KeycloakContainer keycloak;

    static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:25.0.0";
    static String realmImportFile = "test-realm-realm.json";
    static String realmName = "test-realm";
    static String clientId = "test-client";
    static String clientSecret = "9uNx6hi8kRf6hAVfbbMiWbgVJ7o8L7k1";

    @Override
    public Map<String, String> start() {
        keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
                .withRealmImportFile(realmImportFile);
        keycloak.start();

        Map<String, String> config = new HashMap<>();
        config.put("keycloak.url", keycloak.getAuthServerUrl() + "/realms/" + realmName + "/protocol/openid-connect/token");
        config.put("keycloak.client-id", clientId);
        config.put("keycloak.client-secret", clientSecret);
        config.put("mp.jwt.verify.publickey.location", keycloak.getAuthServerUrl() + "/realms/" + realmName + "/protocol/openid-connect/certs");
        config.put("mp.jwt.verify.issuer", keycloak.getAuthServerUrl() + "/realms/" + realmName);
        return config;
    }

    @Override
    public void stop() {
        if (keycloak != null)
            keycloak.stop();
    }
}
