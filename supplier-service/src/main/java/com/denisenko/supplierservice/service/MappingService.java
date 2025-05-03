package com.denisenko.supplierservice.service;

import com.denisenko.supplierservice.dto.MappedProductDto;
import com.denisenko.supplierservice.mapper.MappedProductMapper;
import com.denisenko.supplierservice.model.MappedProduct;
import com.denisenko.supplierservice.model.Supplier;
import com.denisenko.supplierservice.repository.MappedProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MappingService {
    private final MappedProductRepository mappedProductRepository;
    private final MappedProductMapper mappedProductMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:mapping:";

    public void mapProducts(MappedProductDto mappedProductDto, String idempotencyKey) {
        String mappedProductId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (mappedProductId != null) return;

        MappedProduct mappedProduct = mappedProductMapper.toEntity(mappedProductDto);
        mappedProductRepository.save(mappedProduct);
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(mappedProduct.getId()), Duration.ofMinutes(10));
    }

    public List<MappedProductDto> getMappings(List<Integer> systemProductIds) {
        List<MappedProduct> mappedProducts = mappedProductRepository.findAllBySystemProductIdIn(systemProductIds);
        return mappedProductMapper.toDTO(mappedProducts);
    }

    public List<Integer> getSystemIdsBySupplier(Supplier supplier) {
        return mappedProductRepository.findAllSystemProductIdBySupplier(supplier);
    }

    public Map<String, Integer> getSupplierToSystemMappings(List<String> supplierProductIds) {
        return mappedProductRepository.findAllBySupplierProductIdIn(supplierProductIds)
                .stream()
                .collect(Collectors.toMap(MappedProduct::getSupplierProductId, MappedProduct::getSystemProductId));
    }
}
