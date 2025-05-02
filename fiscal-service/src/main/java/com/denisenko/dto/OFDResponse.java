package com.denisenko.dto;

import java.time.LocalDateTime;

public record OFDResponse(String status, String ofdNumber, LocalDateTime timestamp, String errorMessage) {
}
