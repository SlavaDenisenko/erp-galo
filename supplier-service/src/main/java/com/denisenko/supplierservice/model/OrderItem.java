package com.denisenko.supplierservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_order_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private SupplierOrder order;

    @Column(nullable = false)
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;
}
