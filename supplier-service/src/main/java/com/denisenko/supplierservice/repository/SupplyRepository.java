package com.denisenko.supplierservice.repository;

import com.denisenko.supplierservice.model.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyRepository extends JpaRepository<Supply, Integer> {
}
