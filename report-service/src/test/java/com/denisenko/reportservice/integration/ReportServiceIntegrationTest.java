package com.denisenko.reportservice.integration;

import com.denisenko.reportservice.dto.*;
import com.denisenko.reportservice.model.*;
import com.denisenko.reportservice.repository.SaleRepository;
import com.denisenko.reportservice.repository.ShiftRepository;
import com.denisenko.reportservice.service.RedisCacheService;
import com.denisenko.reportservice.service.ReportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReportServiceIntegrationTest extends BaseServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private SaleRepository saleRepository;

    @MockBean
    private RedisCacheService redisCacheService;

    @BeforeEach
    void setUp() {
        initSales();
    }

    @Test
    void createTemplateAndGenerateReport() {
        ReportTemplateDto reportTemplateDto = ReportTemplateDto.builder()
                .name("Sales report")
                .aggregations(List.of(
                        AggregationCriteriaDto.builder()
                                .field("id")
                                .operation("COUNT")
                                .build()
                ))
                .build();

        String idempotencyKey = UUID.randomUUID().toString();
        ReportTemplateDto template = reportService.createTemplate(reportTemplateDto, idempotencyKey);

        Assertions.assertNotNull(template.getId());

        ReportRequest reportRequest = ReportRequest.builder()
                .templateId(template.getId())
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now())
                .build();

        idempotencyKey = UUID.randomUUID().toString();
        ReportResponse reportResponse = reportService.generateReport(reportRequest, idempotencyKey);
        Assertions.assertNotNull(reportResponse);
    }

    private void initSales() {
        Shift shift = Shift.builder()
                .ofdShiftNumber(UUID.randomUUID().toString())
                .startTime(LocalDateTime.now())
                .openedBy("Vyacheslav Denis")
                .build();

        shiftRepository.save(shift);

        List<SaleItem> saleItems1 = List.of(
                SaleItem.builder()
                        .itemId(1)
                        .itemName("Caesar Salad")
                        .price(new BigDecimal(650))
                        .quantity(2.0)
                        .course(1)
                        .status(SaleItemStatus.ACTIVE)
                        .build(),

                SaleItem.builder()
                        .itemId(2)
                        .itemName("Beef Stew")
                        .price(new BigDecimal(990))
                        .quantity(1.0)
                        .course(2)
                        .status(SaleItemStatus.ACTIVE)
                        .build()
        );
        BigDecimal totalPrice1 = saleItems1.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SaleItem> saleItems2 = List.of(
                SaleItem.builder()
                        .itemId(3)
                        .itemName("Shrimp Soup")
                        .price(new BigDecimal(750))
                        .quantity(1.0)
                        .course(1)
                        .status(SaleItemStatus.ACTIVE)
                        .build(),

                SaleItem.builder()
                        .itemId(4)
                        .itemName("Apple Pie")
                        .price(new BigDecimal(500))
                        .quantity(1.0)
                        .course(2)
                        .status(SaleItemStatus.ACTIVE)
                        .build()
        );
        BigDecimal totalPrice2 = saleItems2.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Sale> sales = List.of(
                Sale.builder()
                        .tableNumber(12.1)
                        .numberOfGuests(2)
                        .items(saleItems1)
                        .createdAt(LocalDateTime.now().minusMonths(1))
                        .closedAt(LocalDateTime.now())
                        .totalPrice(totalPrice1)
                        .waiterName("Igor")
                        .paymentMethod(PaymentMethod.CASH)
                        .shift(shift)
                        .build(),

                Sale.builder()
                        .tableNumber(14.0)
                        .items(saleItems2)
                        .createdAt(LocalDateTime.now().minusHours(2))
                        .closedAt(LocalDateTime.now())
                        .totalPrice(totalPrice2)
                        .waiterName("Anna")
                        .paymentMethod(PaymentMethod.CARD)
                        .shift(shift)
                        .build()
        );

        saleItems1.forEach(item -> item.setSale(sales.get(0)));
        saleItems2.forEach(item -> item.setSale(sales.get(1)));

        saleRepository.saveAll(sales);
    }
}
