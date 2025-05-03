package com.denisenko.supplierservice.repository;

import com.denisenko.supplierservice.model.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Integer> {
}
