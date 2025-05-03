package com.denisenko.supplierservice.controller;

import com.denisenko.supplierservice.dto.SupplierDto;
import com.denisenko.supplierservice.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Validated
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(@RequestBody @Valid SupplierDto supplierDto,
                                                      @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(supplierService.createSupplier(supplierDto, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        return new ResponseEntity<>(supplierService.getAllSuppliers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplier(@PathVariable Integer id) {
        return new ResponseEntity<>(supplierService.getSupplier(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDto> updateSupplier(@PathVariable Integer id, @RequestBody @Valid SupplierDto supplierDto) {
        return new ResponseEntity<>(supplierService.updateSupplier(id, supplierDto), HttpStatus.OK);
    }
}
