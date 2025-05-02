package com.denisenko.model;

public class Printer {
    private String name;
    private String ipAddress;
    private int port;
    private String location;
    private String emulatorUrl;

    public Printer(String name, String ipAddress, int port, String location, String emulatorUrl) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.location = location;
        this.emulatorUrl = emulatorUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmulatorUrl() {
        return emulatorUrl;
    }

    public void setEmulatorUrl(String emulatorUrl) {
        this.emulatorUrl = emulatorUrl;
    }
}
