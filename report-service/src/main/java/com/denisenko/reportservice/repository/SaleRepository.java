package com.denisenko.reportservice.repository;

import com.denisenko.reportservice.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByClosedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
}
