package com.denisenko.model;

import java.time.LocalDateTime;

public class Shift {
    private String ofdShiftNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String openedBy;
    private String closedBy;

    public String getOfdShiftNumber() {
        return ofdShiftNumber;
    }

    public void setOfdShiftNumber(String ofdShiftNumber) {
        this.ofdShiftNumber = ofdShiftNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }
}
