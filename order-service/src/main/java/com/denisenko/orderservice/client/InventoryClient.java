package com.denisenko.orderservice.client;

import com.denisenko.orderservice.config.FeignConfig;
import com.denisenko.orderservice.dto.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}", configuration = FeignConfig.class)
public interface InventoryClient {

    //- TODO move path to config
    @GetMapping("/categories")
    CategoryDto getMenu(@RequestParam("categoryType") String categoryType);
}
