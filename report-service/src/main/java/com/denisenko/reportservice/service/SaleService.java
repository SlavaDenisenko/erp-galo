package com.denisenko.reportservice.service;

import com.denisenko.events.OrderClosedEvent;
import com.denisenko.reportservice.mapper.SaleMapper;
import com.denisenko.reportservice.model.Sale;
import com.denisenko.reportservice.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final ShiftService shiftService;
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;

    public void saveClosedOrder(OrderClosedEvent event) {
        Sale sale = saleMapper.toEntity(event);
        sale.setShift(shiftService.getOpenShift());
        sale.setItems(new ArrayList<>(sale.getItems()));
        saleRepository.save(sale);
    }
}
