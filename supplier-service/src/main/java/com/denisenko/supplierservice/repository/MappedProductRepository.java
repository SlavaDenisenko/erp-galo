package com.denisenko.supplierservice.repository;

import com.denisenko.supplierservice.model.MappedProduct;
import com.denisenko.supplierservice.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MappedProductRepository extends JpaRepository<MappedProduct, Integer> {
    List<MappedProduct> findAllBySystemProductIdIn(List<Integer> systemProductIds);

    @Query("SELECT m.systemProductId FROM MappedProduct m WHERE m.supplier = :supplier")
    List<Integer> findAllSystemProductIdBySupplier(@Param("supplier") Supplier supplier);

    List<MappedProduct> findAllBySupplierProductIdIn(List<String> supplierProductIds);
}
