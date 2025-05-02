package com.denisenko.inventoryservice.repository;

import com.denisenko.inventoryservice.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {
}
