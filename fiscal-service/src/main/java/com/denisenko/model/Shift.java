package com.denisenko.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Shift {
    private String ofdShiftNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String openedBy;
    private String closedBy;

    @Override
    public String toString() {
        return "Shift{" +
                "ofdShiftNumber='" + ofdShiftNumber + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", openedBy='" + openedBy + '\'' +
                ", closedBy='" + closedBy + '\'' +
                '}';
    }
}
