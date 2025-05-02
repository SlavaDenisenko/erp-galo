package com.denisenko.reportservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_sale")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false)
    private Double tableNumber;

    @Column(name = "number_of_guests")
    @ColumnDefault("1")
    private Integer numberOfGuests;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "waiter_name", nullable = false)
    private String waiterName;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
    }

    public void setItems(List<SaleItem> items) {
        this.items.clear();
        if (items != null)
            items.forEach(this::addItem);
    }
}
