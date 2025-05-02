package com.denisenko.orderservice.controller;

import com.denisenko.orderservice.dto.MenuDto;
import com.denisenko.orderservice.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<MenuDto> getMenu() {
        return new ResponseEntity<>(menuService.getMenu(), HttpStatus.OK);
    }
}
