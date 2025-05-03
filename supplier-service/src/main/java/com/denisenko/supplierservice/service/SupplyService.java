package com.denisenko.supplierservice.service;

import com.denisenko.events.SupplyReceivedEvent;
import com.denisenko.events.SupplyReceivedItem;
import com.denisenko.supplierservice.dto.SupplyDto;
import com.denisenko.supplierservice.exception.MappingException;
import com.denisenko.supplierservice.exception.PositionNotFoundException;
import com.denisenko.supplierservice.mapper.SupplyMapper;
import com.denisenko.supplierservice.model.Supply;
import com.denisenko.supplierservice.model.SupplyItem;
import com.denisenko.supplierservice.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.denisenko.supplierservice.model.SupplyStatus.*;

@Service
@RequiredArgsConstructor
public class SupplyService {
    private final MappingService mappingService;
    private final KafkaProducerService kafkaProducerService;
    private final SupplyRepository supplyRepository;
    private final SupplyMapper supplyMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:supply:";

    public void receiveSupply(SupplyDto supplyDto, String idempotencyKey) {
        String supplyId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (supplyId != null) return;

        Supply supply = supplyMapper.toEntity(supplyDto);
        supply.setStatus(RECEIVED);
        supply.setItems(new ArrayList<>(supply.getItems()));
        supplyRepository.save(supply);
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(supply.getId()), Duration.ofMinutes(10));
    }

    public List<SupplyDto> getAllSupplies() {
        List<Supply> supplies = supplyRepository.findAll();
        return supplyMapper.toDTO(supplies);
    }

    public SupplyDto getSupply(Integer id) {
        Supply supply = supplyRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Supply with ID " + id + " not found"));
        return supplyMapper.toDTO(supply);
    }

    public void acceptSupply(Integer id, String idempotencyKey) {
        String supplyId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (supplyId != null) return;

        Supply supply = supplyRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Supply with ID = " + id + " not found"));
        try {
            kafkaProducerService.sendSupplyReceivedEvent(createSupplyReceivedEvent(supply));
            supply.setStatus(PROCESSED);
            supplyRepository.save(supply);
        } catch (MappingException e) {
            supply.setStatus(PROCESSING_ERROR);
            supplyRepository.save(supply);
            throw e;
        }
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(supply.getId()), Duration.ofMinutes(10));
    }

    private SupplyReceivedEvent createSupplyReceivedEvent(Supply supply) {
        List<SupplyReceivedItem> items = new ArrayList<>();
        List<String> unmappedItems = new ArrayList<>();
        Map<String, Integer> supplierToSystemIdMap = mappingService.getSupplierToSystemMappings(supply.getItems().stream().map(SupplyItem::getProductId).toList());
        for (SupplyItem supplyItem : supply.getItems()) {
            Integer ingredientId = supplierToSystemIdMap.get(supplyItem.getProductId());
            if (Objects.isNull(ingredientId)) {
                unmappedItems.add(supplyItem.getProductName());
            } else {
                items.add(SupplyReceivedItem.newBuilder()
                        .setIngredientId(ingredientId)
                        .setQuantity(supplyItem.getQuantity())
                        .setCost(supplyItem.getCost())
                        .build());
            }
        }

        if (!unmappedItems.isEmpty())
            throw new MappingException("Mapping failed for the following products: " + String.join(", ", unmappedItems));

        return SupplyReceivedEvent.newBuilder()
                .setDocumentNumber(supply.getNumber())
                .setDocumentDate(supply.getSupplyDate())
                .setAcceptedDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .setSupplierName(supply.getSupplier().getName())
                .setItems(items)
                .build();
    }
}
