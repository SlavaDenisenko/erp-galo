package com.denisenko.supplyorderservice.dto;

import java.time.LocalDateTime;

public record SaleRecord(LocalDateTime date, double quantity) {
}
