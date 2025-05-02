package com.denisenko.service;

import com.denisenko.client.HttpClient;
import com.denisenko.config.PrintConfig;
import com.denisenko.exception.ReceiptPrintException;
import com.denisenko.model.Order;
import com.denisenko.model.Shift;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class PrintService {
    private static final Logger log = LoggerFactory.getLogger(PrintService.class);

    @Inject
    PrintConfig printConfig;

    @Inject
    HttpClient httpClient;

    public void printFiscalShift(Shift shift) {
        retryOperation(() -> httpClient.sendPostRequest(printConfig.url(), printConfig.fiscalShiftPath(), shift, Void.class), "Print fiscal shift");
    }

    public void printFiscalOrder(Order order) {
        retryOperation(() -> httpClient.sendPostRequest(printConfig.url(), printConfig.fiscalOrderPath(), order, Void.class), "Print fiscal order");
    }

    private void retryOperation(PrintOperation operation, String operationName) {
        int attempt = 0;
        int baseDelay = printConfig.retryDelay();

        while (attempt < printConfig.retryAttempts()) {
            try {
                operation.execute();
                return;
            } catch (IOException e) {
                attempt++;
                log.warn("'{}' operation failed (attempt {}/{}): {}", operationName, attempt, printConfig.retryAttempts(), e.getMessage());

                if (attempt >= printConfig.retryAttempts())
                    throw new ReceiptPrintException(operationName + " failed after " + printConfig.retryAttempts() + " attempts");

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

        throw new ReceiptPrintException(operationName + " unexpectedly failed"); //- should never reach this point
    }

    @FunctionalInterface
    interface PrintOperation {
        void execute() throws IOException;
    }
}
