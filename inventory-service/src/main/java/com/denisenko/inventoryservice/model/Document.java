package com.denisenko.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_document")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @Column(name = "document_date", nullable = false)
    private LocalDate documentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "supplier_name")
    private String supplierName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "associatedDocument", orphanRemoval = true)
    private List<InventoryMovement> inventoryMovements = new ArrayList<>();

    public void addInventoryMovement(InventoryMovement inventoryMovement) {
        inventoryMovements.add(inventoryMovement);
        inventoryMovement.setAssociatedDocument(this);
    }

    public void setInventoryMovements(List<InventoryMovement> inventoryMovements) {
        this.inventoryMovements.clear();
        if (inventoryMovements != null)
            inventoryMovements.forEach(this::addInventoryMovement);
    }
}
