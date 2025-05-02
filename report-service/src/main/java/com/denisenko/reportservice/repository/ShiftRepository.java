package com.denisenko.reportservice.repository;

import com.denisenko.reportservice.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    Optional<Shift> findFirstByEndTimeIsNull();

    List<Shift> findAllByEndTimeBetween(LocalDate startDate, LocalDate endDate);
}
