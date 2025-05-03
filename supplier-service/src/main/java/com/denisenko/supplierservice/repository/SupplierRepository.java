package com.denisenko.supplierservice.repository;

import com.denisenko.supplierservice.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findByName(String name);

    @Query("SELECT s FROM Supplier s WHERE :currentDay MEMBER OF s.deliveryDays")
    List<Supplier> findSupplierByDeliveryDay(@Param("currentDay") DayOfWeek currentDay);
}
