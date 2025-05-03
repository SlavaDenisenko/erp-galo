package com.denisenko.client;

import com.denisenko.config.SupplierConfig;
import com.denisenko.exception.SupplierException;
import com.denisenko.model.Supply;
import com.denisenko.model.SupplyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class SupplierClient {
    private static final Logger log = LoggerFactory.getLogger(SupplierClient.class);

    @Inject
    OkHttpClient httpClient;

    @Inject
    SupplierConfig supplierConfig;

    @Inject
    ObjectMapper objectMapper;

    public SupplyResponse sendSupply(Supply supply) {
        String url = prepareUrl(supplierConfig.receiveSupplyPath());
        log.info("Prepare request POST {} with payload {}", url, supply);
        RequestBody requestBody = createRequestBody(supply);

        Request request = new Request.Builder()
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .url(url)
                .post(requestBody)
                .build();

        return executeRequest(request);
    }

    private SupplyResponse executeRequest(Request request) throws SupplierException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorResponse = Objects.nonNull(response.body()) ? response.body().string() : "Unknown error";
                return SupplyResponse.failed("Request failed: " + response.code() + " - " + errorResponse);
            }

            return SupplyResponse.ok();
        } catch (IOException e) {
            log.error("Request failed", e);
            throw new SupplierException("Failed to communicate with GALO", e);
        }
    }

    private RequestBody createRequestBody(Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return RequestBody.create(json, MediaType.parse("application/json"));
        } catch (Exception e) {
            log.error("Failed to serialize payload", e);
            throw new RuntimeException("Failed to serialize payload to JSON", e);
        }
    }

    private String prepareUrl(String path) {
        return supplierConfig.url().replaceAll("/$", "") + "/" + path.replaceAll("^/", "");
    }
}
