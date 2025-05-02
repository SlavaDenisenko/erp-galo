package com.denisenko.orderservice.client;

import com.denisenko.orderservice.config.FeignConfig;
import com.denisenko.orderservice.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "print-service", url = "${print-service.url}", configuration = FeignConfig.class)
public interface PrintClient {

    //- TODO move paths to config
    @PostMapping(value = "/print/order", produces = "text/plain")
    ResponseEntity<String> printOrder(OrderDto orderDto);

    @PostMapping(value = "/print/check", produces = "text/plain")
    ResponseEntity<String> printCheck(OrderDto orderDto);
}
