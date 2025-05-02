package com.denisenko.service;

import com.denisenko.cache.LocalCache;
import com.denisenko.dto.OFDResponse;
import com.denisenko.events.ShiftClosedEvent;
import com.denisenko.events.ShiftOpenedEvent;
import com.denisenko.exception.*;
import com.denisenko.model.Order;
import com.denisenko.model.Shift;
import com.denisenko.repository.RedisRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@ApplicationScoped
public class FiscalService {
    private static final Logger log = LoggerFactory.getLogger(FiscalService.class);
    private static final String SHIFT_KEY = "shift:active";
    private static final String IDEMPOTENCY_PREFIX = "idempotency:fiscal:";

    @Inject
    OFDService ofdService;

    @Inject
    PrintService printService;

    @Inject
    RedisRepository redisRepository;

    @Inject
    ReportService reportService;

    @Inject
    KafkaSender kafkaSender;

    @Inject
    LocalCache localCache;

    @Transactional
    public Shift openShift(String cashierName, String idempotencyKey) {
        Optional<Boolean> exists = redisRepository.findByKey(IDEMPOTENCY_PREFIX + idempotencyKey, Boolean.class);
        if (exists.isPresent()) return getShift();

        Shift shift = createShift(cashierName);
        try {
            OFDResponse ofdResponse = ofdService.openShift(shift);
            shift.setOfdShiftNumber(ofdResponse.ofdNumber());
            kafkaSender.sendShiftOpenedEvent(createShiftOpenedEvent(shift));
            redisRepository.save(SHIFT_KEY, shift);
            redisRepository.save(IDEMPOTENCY_PREFIX + idempotencyKey, Boolean.TRUE);
            printService.printFiscalShift(shift);
            log.info("Shift with OFD number {} was opened", ofdResponse.ofdNumber());
        } catch (OFDException e) {
            log.error("Failed to open shift. Cannot send request to OFD", e);
            throw new ShiftOperationException("Failed to register shift in OFD.");
        } catch (RedisStorageException e) {
            log.error(e.getMessage(), e);
            localCache.save(SHIFT_KEY, shift);
        } catch (ReceiptPrintException e) {
            log.error("Failed to print opening shift.", e);
            throw new ShiftOperationException("Failed to print opening shift. Please try again later.");
        }
        return shift;
    }

    @Transactional
    public Shift closeShift(String cashierName, String idempotencyKey) {
        Optional<Shift> exists = redisRepository.findByKey(IDEMPOTENCY_PREFIX + idempotencyKey, Shift.class);
        if (exists.isPresent()) return exists.get();

        Shift shift = getShift();
        shift.setEndTime(LocalDateTime.now());
        shift.setClosedBy(cashierName);
        try {
            OFDResponse ofdResponse = ofdService.closeShift(shift);
            printService.printFiscalShift(shift);
            kafkaSender.sendShiftClosedEvent(createShiftClosedEvent(shift));
            redisRepository.delete(SHIFT_KEY);
            redisRepository.save(IDEMPOTENCY_PREFIX + idempotencyKey, shift);
            reportService.clearReports();
            log.info("Shift with OFD number {} was closed", ofdResponse.ofdNumber());
        } catch (OFDException e) {
            log.error("Failed to close shift. Cannot send request to OFD", e);
            throw new ShiftOperationException("Failed to close shift in OFD.");
        } catch (ReceiptPrintException e) {
            log.error("Failed to print closing shift.", e);
            throw new ShiftOperationException("Failed to print closing shift. Please try again later.");
        }
        return shift;
    }

    @Transactional
    public OFDResponse closeOrder(Order order, String idempotencyKey) {
        Optional<OFDResponse> exists = redisRepository.findByKey(IDEMPOTENCY_PREFIX + idempotencyKey, OFDResponse.class);
        if (exists.isPresent()) return exists.get();

        //- TODO add shift number to the response field
        Shift shift = getShift();

        try {
            OFDResponse ofdResponse = ofdService.closeOrder(order);
            order.setOfdNumber(ofdResponse.ofdNumber());
            printService.printFiscalOrder(order);
            redisRepository.save(IDEMPOTENCY_PREFIX + idempotencyKey, ofdResponse);
            reportService.updateReports(order);
            log.info("Order with OFD number {} was closed", ofdResponse.ofdNumber());
            return ofdResponse;
        } catch (OFDException e) {
            log.error("Failed to close order. Cannot send request to OFD", e);
            throw new OrderOperationException("Failed to close order in OFD.");
        } catch (ReceiptPrintException e) {
            log.error("Failed to print closing order.", e);
            throw new OrderOperationException("Failed to print closing order. Please try again later.");
        }
    }

    private Shift getShift() {
        Optional<Shift> optionalShift = redisRepository.findByKey(SHIFT_KEY, Shift.class);
        if (optionalShift.isEmpty() || optionalShift.get().getOfdShiftNumber() == null)
            throw new ShiftOperationException("Can't find information about the shift.");

        return optionalShift.get();
    }

    private Shift createShift(String cashierName) {
        Shift shift = new Shift();
        shift.setStartTime(LocalDateTime.now());
        shift.setOpenedBy(cashierName);
        return shift;
    }

    private ShiftOpenedEvent createShiftOpenedEvent(Shift shift) {
        return ShiftOpenedEvent.newBuilder()
                .setOfdShiftNumber(shift.getOfdShiftNumber())
                .setStartTime(shift.getStartTime().atZone(ZoneId.systemDefault()).toInstant())
                .setOpenedBy(shift.getOpenedBy())
                .build();
    }

    private ShiftClosedEvent createShiftClosedEvent(Shift shift) {
        return ShiftClosedEvent.newBuilder()
                .setOfdShiftNumber(shift.getOfdShiftNumber())
                .setStartTime(shift.getStartTime().atZone(ZoneId.systemDefault()).toInstant())
                .setEndTime(shift.getEndTime().atZone(ZoneId.systemDefault()).toInstant())
                .setOpenedBy(shift.getOpenedBy())
                .setClosedBy(shift.getClosedBy())
                .build();
    }
}
