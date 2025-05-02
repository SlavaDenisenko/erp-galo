package com.denisenko.reportservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_report_template")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @ElementCollection
    @CollectionTable(name = "t_template_filter", joinColumns = @JoinColumn(name = "template_id"))
    private List<FilterCriteria> filters = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "t_template_aggregation", joinColumns = @JoinColumn(name = "template_id"))
    private List<AggregationCriteria> aggregations = new ArrayList<>();

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_default", nullable = false, updatable = false)
    @ColumnDefault("false")
    private boolean isDefault;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
