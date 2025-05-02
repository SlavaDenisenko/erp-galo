package com.denisenko.service;

import com.denisenko.config.PrinterConfig;
import com.denisenko.model.Printer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PrinterService {

    @Inject
    PrinterConfig printerConfig;

    public Printer getPrinter(String location) {
        var printer = findPrinterByLocation(location);
        return mapToPrinter(printer);
    }

    public Printer getFiscalPrinter() {
        var printer = printerConfig.printersList().stream()
                .filter(PrinterConfig.Printer::isFiscal)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fiscal printer not found"));

        return mapToPrinter(printer);
    }

    private PrinterConfig.Printer findPrinterByLocation(String location) {
        return printerConfig.printersList().stream()
                .filter(printer -> printer.location().equalsIgnoreCase(location))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Printer not found for location " + location));
    }

    private Printer mapToPrinter(PrinterConfig.Printer printer) {
        return new Printer(
                printer.name(),
                printer.ipAddress(),
                printer.port(),
                printer.location(),
                printer.emulatorUrl()
        );
    }
}
