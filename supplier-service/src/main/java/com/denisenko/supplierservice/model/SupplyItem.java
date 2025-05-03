package com.denisenko.supplierservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "t_supply_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id", nullable = false)
    private Supply supply;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private BigDecimal cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;
}
