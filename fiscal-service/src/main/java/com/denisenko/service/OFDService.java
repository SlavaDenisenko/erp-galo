package com.denisenko.service;

import com.denisenko.client.HttpClient;
import com.denisenko.config.OFDConfig;
import com.denisenko.dto.OFDResponse;
import com.denisenko.exception.OFDException;
import com.denisenko.model.Order;
import com.denisenko.model.Shift;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class OFDService {
    private static final Logger log = LoggerFactory.getLogger(OFDService.class);

    @Inject
    OFDConfig ofdConfig;

    @Inject
    HttpClient httpClient;

    public OFDResponse openShift(Shift shift) {
        return retryOperation(() -> httpClient.sendPostRequest(ofdConfig.url(), ofdConfig.openShiftPath(), shift, OFDResponse.class), "Shift open");
    }

    public OFDResponse closeShift(Shift shift) {
        return retryOperation(() -> httpClient.sendPostRequest(ofdConfig.url(), ofdConfig.closeShiftPath(), shift, OFDResponse.class), "Shift close");
    }

    public OFDResponse closeOrder(Order order) {
        return retryOperation(() -> httpClient.sendPostRequest(ofdConfig.url(), ofdConfig.closeOrderPath(), order, OFDResponse.class), "Order close");
    }

    private OFDResponse retryOperation(OFDOperation operation, String operationName) {
        int attempt = 0;
        int baseDelay = ofdConfig.retryDelay();

        while (attempt < ofdConfig.retryAttempts()) {
            try {
                return operation.execute();
            } catch (IOException e) {
                attempt++;
                log.warn("'{}' operation failed (attempt {}/{}): {}", operationName, attempt, ofdConfig.retryAttempts(), e.getMessage());

                if (attempt >= ofdConfig.retryAttempts())
                    throw new OFDException(operationName + " failed after " + ofdConfig.retryAttempts() + " attempts");

                try {
                    int delay = (int) (baseDelay * Math.pow(2, attempt - 1));
                    log.info("'{}' retrying in {} ms", operationName, delay);
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(operationName + " interrupted during retry", ex);
                }
            }
        }

        throw new OFDException(operationName + " unexpectedly failed"); //- should never reach this point
    }

    @FunctionalInterface
    interface OFDOperation {
        OFDResponse execute() throws IOException;
    }
}
