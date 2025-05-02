package com.denisenko.inventoryservice.controller;

import com.denisenko.inventoryservice.dto.DocumentDto;
import com.denisenko.inventoryservice.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Validated
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentDto> createDocument(@RequestBody @Valid DocumentDto documentDto,
                                                      @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(documentService.createDocument(documentDto, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
        return new ResponseEntity<>(documentService.getAllDocuments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Integer id) {
        return new ResponseEntity<>(documentService.getDocument(id), HttpStatus.OK);
    }
}
