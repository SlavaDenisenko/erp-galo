package com.denisenko.client;

import com.denisenko.config.KeycloakConfig;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class KeycloakClient {
    private static final Logger log = LoggerFactory.getLogger(KeycloakClient.class);

    @Inject
    KeycloakConfig keycloakConfig;

    @Inject
    OkHttpClient httpClient;

    @Inject
    ObjectMapper objectMapper;

    public Map<String, Object> login(String username, String password) throws IOException {
        String body = "grant_type=password" +
                "&client_id=" + keycloakConfig.clientId() +
                "&client_secret=" + keycloakConfig.clientSecret() +
                "&username=" + username +
                "&password=" + password;

        Request request = new Request.Builder()
                .url(keycloakConfig.url())
                .post(RequestBody.create(body, MediaType.parse("application/x-www-form-urlencoded")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Failed to login: " + response.code());

            if (Objects.isNull(response.body()))
                throw new IOException("Response body is null");

            String json = response.body().string();
            log.debug("Response: {}", json);
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonParseException e) {
            log.error("Failed to parse response", e);
            throw new RuntimeException();
        }
    }
}
