package com.denisenko.model;

import java.time.LocalDateTime;

public class OFDResponse {
    private String status;
    private String ofdNumber;
    private LocalDateTime timestamp;
    private String errorMessage;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOfdNumber() {
        return ofdNumber;
    }

    public void setOfdNumber(String ofdNumber) {
        this.ofdNumber = ofdNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
