package com.denisenko.supplierservice.service;

import com.denisenko.supplierservice.dto.SupplierDto;
import com.denisenko.supplierservice.exception.PositionNotFoundException;
import com.denisenko.supplierservice.mapper.SupplierMapper;
import com.denisenko.supplierservice.model.Supplier;
import com.denisenko.supplierservice.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:supplier:";

    @Transactional
    public SupplierDto createSupplier(SupplierDto supplierDto, String idempotencyKey) {
        String supplierId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (supplierId != null) return getSupplier(Integer.parseInt(supplierId));

        Supplier supplier = supplierMapper.toEntity(supplierDto);
        supplier.setApiMethods(new ArrayList<>(supplier.getApiMethods()));
        supplierRepository.save(supplier);
        log.info("Supplier '{}' is saved with ID = {}", supplier.getName(), supplier.getId());
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(supplier.getId()), Duration.ofMinutes(10));
        return supplierMapper.toDTO(supplier);
    }

    public List<SupplierDto> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return supplierMapper.toDTO(suppliers);
    }

    public List<Supplier> getSuppliersWithTodayDelivery() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return getSuppliersByDeliveryDay(today);
    }

    public List<Supplier> getSuppliersByDeliveryDay(DayOfWeek dayOfWeek) {
        return supplierRepository.findSupplierByDeliveryDay(dayOfWeek);
    }

    public SupplierDto getSupplier(Integer id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Supplier with ID = " + id + " not found"));
        return supplierMapper.toDTO(supplier);
    }

    public Supplier getSupplier(String supplierName) {
        return supplierRepository.findByName(supplierName).orElseThrow(() -> new PositionNotFoundException("Supplier with name = " + supplierName + " not found"));
    }

    public SupplierDto updateSupplier(Integer id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Supplier with ID = " + id + " not found"));
        Supplier updatedSupplier = supplierMapper.toEntity(supplierDto);
        updatedSupplier.setId(supplier.getId());
        updatedSupplier.setApiMethods(new ArrayList<>(updatedSupplier.getApiMethods()));
        supplierRepository.save(updatedSupplier);
        return supplierMapper.toDTO(updatedSupplier);
    }
}
