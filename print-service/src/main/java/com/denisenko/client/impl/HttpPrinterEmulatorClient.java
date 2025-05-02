package com.denisenko.client.impl;

import com.denisenko.client.PrinterClient;
import com.denisenko.model.Printer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class HttpPrinterEmulatorClient implements PrinterClient {
    private static final Logger log = LoggerFactory.getLogger(HttpPrinterEmulatorClient.class);

    @Inject
    Client client;

    @Override
    public Optional<String> sendToPrinter(Printer printer, String receipt) {
        log.info("Prepare request POST {}", printer.getEmulatorUrl());
        try (Response response = client
                .target(printer.getEmulatorUrl())
                .request()
                .post(Entity.entity(receipt, MediaType.TEXT_HTML))) {

            if (response.getStatus() != 200) {
                log.error("Request failed... Response code: {}", response.getStatus());
                String errorMessage = response.readEntity(String.class);
                return Optional.of("Failed to print on printer " + printer.getName() + " (" + printer.getIpAddress() + "). Reason: " + errorMessage);
            }

            log.info("Request completed successfully!");
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error occurred while communicate with printer {} ({})", printer.getName(), printer.getIpAddress(), e);
            return Optional.of("Failed to print on printer " + printer.getName() + " (" + printer.getIpAddress() + "). Reason: " + e.getMessage());
        }
    }
}
