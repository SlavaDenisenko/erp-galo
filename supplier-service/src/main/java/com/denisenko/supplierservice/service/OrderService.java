package com.denisenko.supplierservice.service;

import com.denisenko.supplierservice.client.SupplierClientFactory;
import com.denisenko.supplierservice.dto.MappedProductDto;
import com.denisenko.supplierservice.dto.OrderItemDto;
import com.denisenko.supplierservice.dto.SupplierDto;
import com.denisenko.supplierservice.dto.SupplierOrderDto;
import com.denisenko.supplierservice.exception.MappingException;
import com.denisenko.supplierservice.exception.OrderAlreadySentException;
import com.denisenko.supplierservice.exception.PositionNotFoundException;
import com.denisenko.supplierservice.mapper.SupplierOrderMapper;
import com.denisenko.supplierservice.model.SupplierOrder;
import com.denisenko.supplierservice.repository.SupplierOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.denisenko.supplierservice.model.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final MappingService mappingService;
    private final SupplierOrderRepository supplierOrderRepository;
    private final SupplierOrderMapper supplierOrderMapper;
    private final SupplierService supplierService;
    private final SupplierClientFactory supplierClientFactory;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:supplierorder:";

    public SupplierOrderDto createOrder(SupplierOrderDto supplierOrderDto, String idempotencyKey) {
        String orderId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (orderId != null) return getOrder(Integer.parseInt(orderId));

        SupplierOrder order = supplierOrderMapper.toEntity(supplierOrderDto);
        order.setItems(new ArrayList<>(order.getItems()));
        supplierOrderRepository.save(order);
        log.info("Order for supplier [{}] is saved with ID = {}", order.getSupplier().getId(), order.getId());
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(order.getId()), Duration.ofMinutes(10));
        return supplierOrderMapper.toDTO(order);
    }

    public List<SupplierOrderDto> getAllOrders() {
        List<SupplierOrder> orders = supplierOrderRepository.findAll();
        return supplierOrderMapper.toDTO(orders);
    }

    public SupplierOrderDto getOrder(Integer id) {
        SupplierOrder supplierOrder = supplierOrderRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Supplier order with ID " + id + " not found"));
        return supplierOrderMapper.toDTO(supplierOrder);
    }

    public SupplierOrderDto updateOrder(Integer id, SupplierOrderDto supplierOrderDto) {
        SupplierOrder supplierOrder = supplierOrderRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Supplier order with ID " + id + " not found"));
        if (supplierOrder.getStatus() == SENT) {
            throw OrderAlreadySentException.forOrderId(supplierOrder.getId());
        }

        SupplierOrder updatedSupplierOrder = supplierOrderMapper.toEntity(supplierOrderDto);
        supplierOrder.setItems(new ArrayList<>(updatedSupplierOrder.getItems()));
        supplierOrderRepository.save(supplierOrder);
        return supplierOrderMapper.toDTO(supplierOrder);
    }

    public String sendOrder(Integer id, String idempotencyKey) {
        String exists = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (exists != null) return exists;

        SupplierOrderDto supplierOrderDto = getOrder(id);
        SupplierOrder supplierOrder = supplierOrderMapper.toEntity(supplierOrderDto);
        SupplierDto supplierDto = supplierService.getSupplier(supplierOrderDto.getSupplierId());

        try {
            supplierOrderDto.setItems(mapSystemToSupplier(supplierOrderDto.getItems()));
        } catch (MappingException e) {
            supplierOrder.setStatus(PROCESSING_ERROR);
            supplierOrderRepository.save(supplierOrder);
            throw e;
        }

        supplierOrderDto.setSupplierName(supplierDto.getName());
        String supplierOrderId = supplierClientFactory.getClient(supplierDto).sendOrder(supplierDto, supplierOrderDto);
        supplierOrder.setStatus(SENT);
        supplierOrder.setSupplierOrderId(supplierOrderId);
        supplierOrderRepository.save(supplierOrder);
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, supplierOrderId, Duration.ofMinutes(10));
        return supplierOrderId;
    }

    private List<OrderItemDto> mapSystemToSupplier(List<OrderItemDto> items) {
        Map<String, MappedProductDto> mappings = mappingService.getMappings(items.stream().map(OrderItemDto::getProductId).map(Integer::parseInt).toList()).stream()
                .collect(Collectors.toMap(mp -> String.valueOf(mp.getInventoryProductId()), Function.identity()));
        List<String> unmappedItems = new ArrayList<>();
        for (OrderItemDto item : items) {
            MappedProductDto mappedProduct = mappings.get(item.getProductId());
            if (Objects.isNull(mappedProduct)) {
                unmappedItems.add(item.getProductName());
            } else {
                item.setProductId(mappedProduct.getSupplierProductId());
                item.setProductName(mappedProduct.getSupplierProductName());
            }
        }

        if (!unmappedItems.isEmpty())
            throw new MappingException("Mapping failed for the following products: " + String.join(", ", unmappedItems));

        return items;
    }
}
