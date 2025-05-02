package com.denisenko.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveItemsRequest {
    private List<Integer> itemIds;
    @NotBlank(message = "Reason for deletion is required")
    private String reasonForDeletion;
}
