package com.denisenko.supplierservice.controller;

import com.denisenko.supplierservice.dto.SupplierOrderDto;
import com.denisenko.supplierservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<SupplierOrderDto> createOrder(@RequestBody @Valid SupplierOrderDto supplierOrderDto,
                                                        @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(orderService.createOrder(supplierOrderDto, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SupplierOrderDto>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierOrderDto> getOrder(@PathVariable Integer id) {
        return new ResponseEntity<>(orderService.getOrder(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierOrderDto> updateOrder(@PathVariable Integer id, @RequestBody @Valid SupplierOrderDto supplierOrderDto) {
        return new ResponseEntity<>(orderService.updateOrder(id, supplierOrderDto), HttpStatus.OK);
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendOrder(@PathVariable Integer id,
                                            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(orderService.sendOrder(id, idempotencyKey), HttpStatus.OK);
    }
}
