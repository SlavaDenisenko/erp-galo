package com.denisenko.service;

import com.denisenko.client.KeycloakClient;
import com.denisenko.config.KeycloakConfig;
import com.denisenko.dto.AuthResponse;
import com.denisenko.dto.RoleResponse;
import com.denisenko.dto.UserCredentials;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final String ACCESS_TOKEN = "access_token";
    private static final String TOKEN_TYPE = "token_type";
    private static final String EXPIRES_IN = "expires_in";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";

    @Inject
    KeycloakClient keycloakClient;

    @Inject
    KeycloakConfig keycloakConfig;

    public AuthResponse login(UserCredentials userCredentials) {
        log.info("Login user with username '{}'", userCredentials.username());
        Map<String, Object> response;
        try {
            response = keycloakClient.login(userCredentials.username(), userCredentials.password());
        } catch (IOException e) {
            log.error("Failed to login to keycloak", e);
            throw new RuntimeException("Failed to login to keycloak");
        }

        String accessToken = (String) response.get(ACCESS_TOKEN);
        String tokenType = (String) response.get(TOKEN_TYPE);
        String expiresIn = String.valueOf(response.get(EXPIRES_IN));

        return new AuthResponse(accessToken, tokenType, expiresIn);
    }

    public RoleResponse getRole(JsonWebToken jwt) {
        Map<String, Map<String, List<Object>>> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (Objects.nonNull(resourceAccess) && resourceAccess.containsKey(keycloakConfig.clientId())) {
            List<Object> roles = resourceAccess.get(keycloakConfig.clientId()).get(ROLES);
            if (!roles.isEmpty()) {
                String role = roles.get(0).toString().replaceAll("\"", "");
                return new RoleResponse(role);
            }
        }
        return new RoleResponse(null);
    }
}
