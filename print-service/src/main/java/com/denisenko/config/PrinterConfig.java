package com.denisenko.config;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "printers")
public interface PrinterConfig {
    List<Printer> printersList();

    interface Printer {
        String name();

        String ipAddress();

        int port();

        String location();

        String emulatorUrl();

        boolean isFiscal();
    }
}
