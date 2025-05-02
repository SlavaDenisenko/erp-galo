package com.denisenko.orderservice.controller;

import com.denisenko.orderservice.dto.OrderDto;
import com.denisenko.orderservice.dto.OrderItemDto;
import com.denisenko.orderservice.dto.OrderResponse;
import com.denisenko.orderservice.dto.RemoveItemsRequest;
import com.denisenko.orderservice.model.PaymentMethod;
import com.denisenko.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderDto orderDto,
                                                     @RequestHeader("X-User") String waiterName,
                                                     @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(orderService.createOrder(orderDto, waiterName, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/closed")
    public ResponseEntity<List<OrderDto>> getAllClosedOrders() {
        return new ResponseEntity<>(orderService.getAllClosedOrders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String id) {
        return new ResponseEntity<>(orderService.getOrder(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.OK)
    public void closeOrder(@PathVariable String id, @RequestBody PaymentMethod paymentMethod,
                           @RequestHeader("Idempotency-Key") String idempotencyKey) {
        orderService.closeOrder(id, paymentMethod, idempotencyKey);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addItems(@PathVariable String id, @RequestBody @Valid List<OrderItemDto> items,
                                                  @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(orderService.addItems(id, items, idempotencyKey), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItems(@PathVariable String id, @RequestBody @Valid RemoveItemsRequest request) {
        orderService.deleteItems(id, request);
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<OrderResponse> printCheck(@PathVariable String id, @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(orderService.printCheck(id, idempotencyKey), HttpStatus.OK);
    }
}
