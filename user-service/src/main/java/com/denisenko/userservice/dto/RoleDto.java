package com.denisenko.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDto {
    private String id;
    @NotBlank(message = "Role name cannot be blank")
    private String name;
    private String description;
}
