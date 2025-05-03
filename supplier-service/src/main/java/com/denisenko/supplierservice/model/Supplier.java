package com.denisenko.supplierservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_supplier")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierStatus status;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "t_supplier_delivery_day", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "delivery_day", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> deliveryDays;

    @Column(name = "api_url", nullable = false)
    private String apiUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "api_type", nullable = false)
    private ApiType apiType;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierApiMethod> apiMethods = new ArrayList<>();

    public void addApiMethod(SupplierApiMethod apiMethod) {
        apiMethods.add(apiMethod);
        apiMethod.setSupplier(this);
    }

    public void setApiMethods(List<SupplierApiMethod> apiMethods) {
        this.apiMethods.clear();
        if (apiMethods != null)
            apiMethods.forEach(this::addApiMethod);
    }
}
