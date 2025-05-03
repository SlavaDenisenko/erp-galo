package com.denisenko.supplierservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_mapped_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MappedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "supplier_product_id", nullable = false)
    private String supplierProductId;

    @Column(name = "supplier_product_name", nullable = false)
    private String supplierProductName;

    @Column(name = "inventory_product_id", nullable = false)
    private Integer systemProductId;

    @Column(name = "inventory_product_name", nullable = false)
    private String systemProductName;
}
