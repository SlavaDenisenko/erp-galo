package com.denisenko.orderservice.service;

import com.denisenko.orderservice.client.PrintClient;
import com.denisenko.orderservice.dto.OrderDto;
import com.denisenko.orderservice.dto.PrintResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintService {
    private final PrintClient printClient;

    public PrintResult printOrder(OrderDto orderDto) {
        return executeOperation(() -> printClient.printOrder(orderDto), "Print order");
    }

    public PrintResult printCheck(OrderDto orderDto) {
        return executeOperation(() -> printClient.printCheck(orderDto), "Print check");
    }

    private PrintResult executeOperation(PrintOperation operation, String operationName) {
        try {
            ResponseEntity<String> response = operation.execute();

            if (!response.getStatusCode().is2xxSuccessful()) {
                return PrintResult.failed("Print failed: " + response.getBody());
            }

            return PrintResult.ok();
        } catch (Exception e) {
            log.error("'{}' operation. Unexpected exception during printing: ", operationName, e);
            return PrintResult.failed("Print exception: " + e.getMessage());
        }
    }

    @FunctionalInterface
    interface PrintOperation {
        ResponseEntity<String> execute();
    }
}
