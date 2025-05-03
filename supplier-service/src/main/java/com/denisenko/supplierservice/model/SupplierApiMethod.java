package com.denisenko.supplierservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_supplier_api_method")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplierApiMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_name", nullable = false)
    private ApiAction actionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "http_method", nullable = false)
    private HttpMethod httpMethod;

    @Column(nullable = false)
    private String path;
}
