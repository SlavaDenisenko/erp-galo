package com.denisenko.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

@ApplicationScoped
public class HttpClient {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    @Inject
    OkHttpClient httpClient;

    @Inject
    ObjectMapper objectMapper;

    public <T, R> R sendPostRequest(String baseUrl, String path, T payload, Class<R> responseType) throws IOException {
        String url = prepareUrl(baseUrl, path);
        log.info("Prepare request POST {} with payload {}", url, payload);
        RequestBody requestBody = createRequestBody(payload);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return executeRequest(request, responseType);
    }

    private <R> R executeRequest(Request request, Class<R> responseType) throws IOException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorResponse = Objects.nonNull(response.body()) ? response.body().string() : "Unknown error";
                log.error("Request failed: {}", errorResponse);
                throw new IOException("Request failed: " + response.code() + " - " + errorResponse);
            }

            if (Objects.isNull(response.body())) {
                return null;
            }

            String bodyString = response.body().string();
            log.info("Request completed successfully.Response: {}", bodyString);
            if (bodyString.trim().isEmpty()) {
                return null;
            }

            return objectMapper.readValue(bodyString, responseType);
        }
    }

    private <T> RequestBody createRequestBody(T payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return RequestBody.create(json, MediaType.parse("application/json"));
        } catch (Exception e) {
            log.error("Failed to serialize payload", e);
            throw new RuntimeException("Failed to serialize payload to JSON", e);
        }
    }

    private String prepareUrl(String baseUrl, String path) {
        return baseUrl.replaceAll("/$", "") + "/" + path.replaceAll("^/", "");
    }
}
