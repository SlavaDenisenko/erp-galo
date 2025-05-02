package com.denisenko.reportservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_shift")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ofd_shift_number", nullable = false)
    private String ofdShiftNumber;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "opened_by", nullable = false)
    private String openedBy;

    @Column(name = "closed_by")
    private String closedBy;

    @OneToMany(mappedBy = "shift")
    private List<Sale> sales;
}
