package com.denisenko.orderservice.client;

import com.denisenko.orderservice.config.FeignConfig;
import com.denisenko.orderservice.dto.OFDResponse;
import com.denisenko.orderservice.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "fiscal-service", url = "${fiscal-service.url}", configuration = FeignConfig.class)
public interface FiscalClient {

    //- TODO move path to config
    @PostMapping(value = "/fiscal/order/close")
    OFDResponse closeOrder(@RequestBody OrderDto orderDto, @RequestHeader String idempotencyKey);
}
