package com.denisenko.supplierservice.controller;

import com.denisenko.supplierservice.dto.SupplyDto;
import com.denisenko.supplierservice.service.SupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplies")
@RequiredArgsConstructor
@Validated
public class SupplyController {
    private final SupplyService supplyService;

    @PostMapping("/receive")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receiveSupply(@RequestBody @Valid SupplyDto supplyDto,
                              @RequestHeader("Idempotency-Key") String idempotencyKey) {
        supplyService.receiveSupply(supplyDto, idempotencyKey);
    }

    @GetMapping
    public ResponseEntity<List<SupplyDto>> getAllSupplies() {
        return new ResponseEntity<>(supplyService.getAllSupplies(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyDto> getSupply(@PathVariable Integer id) {
        return new ResponseEntity<>(supplyService.getSupply(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void acceptSupply(@PathVariable Integer id,
                             @RequestHeader("Idempotency-Key") String idempotencyKey) {
        supplyService.acceptSupply(id, idempotencyKey);
    }
}
