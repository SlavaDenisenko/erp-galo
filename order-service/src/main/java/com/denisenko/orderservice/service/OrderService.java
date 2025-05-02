package com.denisenko.orderservice.service;

import com.denisenko.orderservice.dto.*;
import com.denisenko.orderservice.exception.PositionNotFoundException;
import com.denisenko.orderservice.mapper.OrderItemMapper;
import com.denisenko.orderservice.mapper.OrderMapper;
import com.denisenko.orderservice.model.Order;
import com.denisenko.orderservice.model.OrderItem;
import com.denisenko.orderservice.model.PaymentMethod;
import com.denisenko.orderservice.repository.OrderRepository;
import com.denisenko.orderservice.statemachine.OrderClosedState;
import com.denisenko.orderservice.statemachine.OrderSagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.denisenko.orderservice.model.OrderItemStatus.ACTIVE;
import static com.denisenko.orderservice.model.OrderItemStatus.DELETED;
import static com.denisenko.orderservice.statemachine.OrderClosedState.COMPLETED;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final PrintService printService;
    private final OrderSagaService orderSagaService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final MenuService menuService;
    private final RedisCacheService redisCacheService;

    public static final String IDEMPOTENCY_PREFIX = "idempotency:order:";

    @Transactional
    public OrderResponse createOrder(OrderDto orderDto, String waiterName, String idempotencyKey) {
        OrderResponse exists = redisCacheService.get(IDEMPOTENCY_PREFIX + idempotencyKey, OrderResponse.class);
        if (exists != null) return exists;

        orderDto.setWaiterName(waiterName);
        Order order = orderMapper.toEntity(orderDto);
        order.setId(UUID.randomUUID().toString());
        fillOrderItems(order.getItems());
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(calculateTotalPrice(order.getItems()));
        orderRepository.save(order);
        PrintResult printResult = printService.printOrder(orderMapper.toDTO(order));
        return prepareAndSaveOrderResponse(order.getId(), printResult, idempotencyKey);
    }

    public List<OrderDto> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orderMapper.toDTO(orders);
    }

    public List<OrderDto> getAllClosedOrders() {
        List<Order> closedOrders = toList(orderRepository.findAll()).stream()
                .filter(order -> order.getClosedAt() != null)
                .toList();
        return orderMapper.toDTO(closedOrders);
    }

    public OrderDto getOrder(String id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Order with ID " + id + " not found"));
        return orderMapper.toDTO(order);
    }

    @Transactional
    public void closeOrder(String id, PaymentMethod paymentMethod, String idempotencyKey) {
        OrderClosedState orderClosedState = redisCacheService.get(IDEMPOTENCY_PREFIX + idempotencyKey, OrderClosedState.class);
        if (COMPLETED == orderClosedState) return;
        Order order = orderRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Order with ID " + id + " not found"));
        order.setPaymentMethod(paymentMethod);
        OrderDto orderDto = orderMapper.toDTO(order);
        orderSagaService.startOrderClosedSaga(orderDto, idempotencyKey);
    }

    @Transactional
    public OrderResponse addItems(String id, List<OrderItemDto> itemsDto, String idempotencyKey) {
        OrderResponse exists = redisCacheService.get(IDEMPOTENCY_PREFIX + idempotencyKey, OrderResponse.class);
        if (exists != null) return exists;

        Order order = orderRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Order with ID " + id + " not found"));
        List<OrderItem> items = itemsDto.stream()
                .map(orderItemMapper::toEntity)
                .toList();
        fillOrderItems(items);
        order.getItems().addAll(items);
        order.setTotalPrice(calculateTotalPrice(order.getItems()));
        orderRepository.save(order);
        PrintResult printResult = printService.printOrder(orderMapper.toDTO(order));
        return prepareAndSaveOrderResponse(id, printResult, idempotencyKey);
    }

    @Transactional
    public void deleteItems(String id, RemoveItemsRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Order with ID " + id + " not found"));
        order.getItems().stream()
                .filter(item -> request.getItemIds().stream().anyMatch(itemId -> itemId.equals(item.getItemId())))
                .forEach(item -> {
                    item.setStatus(DELETED);
                    item.setReasonForDeletion(request.getReasonForDeletion());
                });
        order.setTotalPrice(calculateTotalPrice(order.getItems()));
        orderRepository.save(order);
    }

    @Transactional
    public OrderResponse printCheck(String id, String idempotencyKey) {
        OrderResponse exists = redisCacheService.get(IDEMPOTENCY_PREFIX + idempotencyKey, OrderResponse.class);
        if (exists != null) return exists;

        Order order = orderRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Order with ID " + id + " not found"));
        OrderDto orderDto = orderMapper.toDTO(order);
        PrintResult printResult = printService.printCheck(orderDto);
        return prepareAndSaveOrderResponse(id, printResult, idempotencyKey);
    }

    private OrderResponse prepareAndSaveOrderResponse(String orderId, PrintResult printResult, String idempotencyKey) {
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(orderId)
                .printed(printResult.success())
                .printMessage(printResult.message())
                .build();
        redisCacheService.put(IDEMPOTENCY_PREFIX + idempotencyKey, orderResponse);
        return orderResponse;
    }

    private void fillOrderItems(List<OrderItem> items) {
        List<RecipeDto> recipes = menuService.getRecipes(items.stream().map(OrderItem::getItemId).toList());
        for (OrderItem item : items) {
            Optional<RecipeDto> recipe = recipes.stream().filter(r -> r.getId().equals(item.getItemId())).findFirst();
            if (recipe.isEmpty()) {
                log.warn("Recipe '{}' with ID = {} wasn't found!", item.getItemName(), item.getItemId());
                continue;
            }

            item.setStatus(ACTIVE);
            item.setItemName(recipe.get().getName());
            item.setPrice(recipe.get().getPrice());
            item.setLocation(recipe.get().getLocation());
        }
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : items) {
            if (item.getStatus() == DELETED) continue;
            totalPrice = totalPrice.add(BigDecimal.valueOf(item.getQuantity()).multiply(item.getPrice()));
        }
        return totalPrice;
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
