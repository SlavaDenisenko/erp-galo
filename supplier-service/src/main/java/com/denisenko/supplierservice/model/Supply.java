package com.denisenko.supplierservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_supply")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private String number;

    @OneToMany(mappedBy = "supply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyStatus status;

    @Column(name = "supply_date", nullable = false)
    private LocalDate supplyDate;

    public void addItem(SupplyItem item) {
        items.add(item);
        item.setSupply(this);
    }

    public void setItems(List<SupplyItem> items) {
        this.items.clear();
        if (items != null)
            items.forEach(this::addItem);
    }
}
