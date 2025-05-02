package com.denisenko.reportservice.service;

import com.denisenko.events.ShiftClosedEvent;
import com.denisenko.events.ShiftOpenedEvent;
import com.denisenko.reportservice.model.Shift;
import com.denisenko.reportservice.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;

    public void saveOpenedShift(ShiftOpenedEvent event) {
        Shift shift = mapOpenedShiftToShift(event);
        shiftRepository.save(shift);
    }

    @Transactional
    public void saveClosedShift(ShiftClosedEvent event) {
        Shift shift = getOpenShift();
        shift.setEndTime(event.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        shift.setClosedBy(event.getClosedBy());
        shiftRepository.save(shift);
    }

    public Shift getOpenShift() {
        return shiftRepository.findFirstByEndTimeIsNull()
                .orElseThrow(() -> new RuntimeException("No open shift found"));
    }

    private Shift mapOpenedShiftToShift(ShiftOpenedEvent event) {
        return Shift.builder()
                .ofdShiftNumber(event.getOfdShiftNumber())
                .startTime(event.getStartTime().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .openedBy(event.getOpenedBy())
                .build();
    }
}
