package com.denisenko.service;

import com.denisenko.client.PrinterClient;
import com.denisenko.model.Order;
import com.denisenko.model.OrderItem;
import com.denisenko.model.Printer;
import com.denisenko.model.Shift;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class PrintService {
    private static final Logger log = LoggerFactory.getLogger(PrintService.class);

    @Inject
    PrinterClient printerClient;

    @Inject
    PrinterService printerService;

    @Inject
    TemplateService templateService;

    public String printOrder(Order order) {
        Map<String, List<OrderItem>> itemsByLocation = order.getItems().stream()
                .collect(Collectors.groupingBy(OrderItem::getLocation));
        StringBuilder builder = new StringBuilder();
        itemsByLocation.forEach((location, items) -> {
            Printer printer = printerService.getPrinter(location);
            String receipt = templateService.generateReceipt(order, items);
            log.debug("Receipt: {}", receipt);
            Optional<String> printerResponse = printerClient.sendToPrinter(printer, receipt);
            printerResponse.ifPresent(response -> builder.append(response).append("\n"));
        });
        return builder.toString();
    }

    public String printCheck(Order order) {
        String preCheck = templateService.generatePreCheck(order);
        log.debug("Pre-check: {}", preCheck);
        //- TODO not only fiscal
        return printFiscal(preCheck);
    }

    public String printFiscalShift(Shift shift) {
        String shiftReceipt = templateService.generateShiftReceipt(shift);
        log.debug("Shift Receipt: {}", shiftReceipt);
        return printFiscal(shiftReceipt);
    }

    public String printFiscalOrder(Order order) {
        String fiscalReceipt = templateService.generateFiscalReceipt(order);
        log.debug("Fiscal Receipt: {}", fiscalReceipt);
        return printFiscal(fiscalReceipt);
    }

    private String printFiscal(String text) {
        Printer printer = printerService.getFiscalPrinter();
        Optional<String> printerResponse = printerClient.sendToPrinter(printer, text);
        return printerResponse.orElse("");
    }
}
