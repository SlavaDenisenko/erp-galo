package com.denisenko.supplierservice.scheduler;

import com.denisenko.events.SupplyRequestEvent;
import com.denisenko.supplierservice.config.SchedulerConfig;
import com.denisenko.supplierservice.model.RequestLog;
import com.denisenko.supplierservice.model.Supplier;
import com.denisenko.supplierservice.service.KafkaProducerService;
import com.denisenko.supplierservice.service.MappingService;
import com.denisenko.supplierservice.service.RequestService;
import com.denisenko.supplierservice.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierScheduler {
    private final SchedulerConfig schedulerConfig;
    private final SupplierService supplierService;
    private final RequestService requestService;
    private final MappingService mappingService;
    private final KafkaProducerService kafkaProducerService;

    //@Scheduled(cron = "#{@schedulerConfig.supplierSchedulerCron}")
    @Scheduled(
            initialDelayString = "#{@schedulerConfig.supplierInitialDelay}",
            fixedRateString = "#{@schedulerConfig.supplierFixedRate}"
    )
    public void processSuppliers() {
        log.info("Starting supplier processing: searching for suppliers scheduled to deliver today.");
        List<RequestLog> requests = new ArrayList<>();
        List<Supplier> suppliers = supplierService.getSuppliersWithTodayDelivery();
        for (Supplier supplier : suppliers) {
            List<Integer> systemProductIds = mappingService.getSystemIdsBySupplier(supplier);
            RequestLog request = prepareRequest(supplier);
            requests.add(request);
            kafkaProducerService.sendSupplyOrderRequest(new SupplyRequestEvent(request.getRequestId(), systemProductIds.stream().map(String::valueOf).toList()));
        }
        log.info("Prepared {} requests for today's delivery.", requests.size());
        requestService.saveAll(requests);
    }

    private RequestLog prepareRequest(Supplier supplier) {
        return RequestLog.builder()
                .requestId(UUID.randomUUID().toString())
                .supplier(supplier)
                .build();
    }
}
