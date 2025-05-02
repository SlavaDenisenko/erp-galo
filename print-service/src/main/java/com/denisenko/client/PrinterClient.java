package com.denisenko.client;

import com.denisenko.model.Printer;

import java.util.Optional;

public interface PrinterClient {

    Optional<String> sendToPrinter(Printer printer, String receipt);
}
