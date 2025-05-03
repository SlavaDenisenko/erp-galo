package com.denisenko.supplierservice.controller;

import com.denisenko.supplierservice.dto.MappedProductDto;
import com.denisenko.supplierservice.service.MappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mappings")
@RequiredArgsConstructor
@Validated
public class MappingController {
    private final MappingService mappingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void mapProducts(@RequestBody @Valid MappedProductDto mappedProductDto,
                            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        mappingService.mapProducts(mappedProductDto, idempotencyKey);
    }
}
