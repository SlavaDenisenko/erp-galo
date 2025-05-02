package com.denisenko.reportservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "t_sale_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Integer course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleItemStatus status;

    @Column(name = "reason_for_deletion")
    private String reasonForDeletion;
}
