package com.denisenko.supplierservice.repository;

import com.denisenko.supplierservice.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestLog, Long> {
    Optional<RequestLog> findByRequestId(String requestId);
}
