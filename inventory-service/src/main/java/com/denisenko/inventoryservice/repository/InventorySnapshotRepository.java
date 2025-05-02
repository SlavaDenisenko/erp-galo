package com.denisenko.inventoryservice.repository;

import com.denisenko.inventoryservice.model.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Integer> {
}
