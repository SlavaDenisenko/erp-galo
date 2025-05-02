package com.denisenko.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_inventory_snapshot")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventorySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double stock;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDateTime snapshotDate;

    @PrePersist
    protected void onCreate() {
        snapshotDate = LocalDateTime.now();
    }
}
