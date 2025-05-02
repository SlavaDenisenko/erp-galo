package com.denisenko.service;

import com.denisenko.events.ShiftClosedEvent;
import com.denisenko.events.ShiftOpenedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@ApplicationScoped
public class KafkaSender {
    private static final Logger log = LoggerFactory.getLogger(KafkaSender.class);

    @Channel("shift-opened")
    Emitter<ShiftOpenedEvent> shiftOpenedEmitter;

    @Channel("shift-closed")
    Emitter<ShiftClosedEvent> shiftClosedEmitter;

    public void sendShiftOpenedEvent(ShiftOpenedEvent event) {
        log.info("Preparing to send shift opened event: {}", event);
        shiftOpenedEmitter.send(event)
                .whenComplete((success, failure) -> {
                    if (Objects.nonNull(failure))
                        log.error("Failed to send shift opened event: {}", failure.getMessage(), failure);
                    else
                        log.info("Shift opened event sent successfully");
                });
    }

    public void sendShiftClosedEvent(ShiftClosedEvent event) {
        log.info("Preparing to send shift closed event: {}", event);
        shiftClosedEmitter.send(event)
                .whenComplete((success, failure) -> {
                    if (Objects.nonNull(failure))
                        log.error("Failed to send shift closed event: {}", failure.getMessage(), failure);
                    else
                        log.info("Shift closed event sent successfully");
                });
    }
}
