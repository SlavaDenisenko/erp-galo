package com.denisenko.inventoryservice.unit.validation;

import com.denisenko.inventoryservice.dto.DocumentDto;
import com.denisenko.inventoryservice.model.DocumentType;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DocumentDtoValidationTest extends BaseValidationTest {

    @Test
    void validDocumentTest() {
        DocumentDto documentDto = DocumentDto.builder()
                .documentNumber("P800-901")
                .documentDate(LocalDate.of(2024, Month.SEPTEMBER, 14))
                .documentType(DocumentType.SUPPLY.name())
                .supplierName("RTK")
                .build();

        Set<ConstraintViolation<DocumentDto>> violations = validator.validate(documentDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void missingDocumentNumberTest() {
        DocumentDto documentDto = DocumentDto.builder()
                .documentDate(LocalDate.of(2024, Month.SEPTEMBER, 14))
                .documentType(DocumentType.SUPPLY.name())
                .supplierName("RTK")
                .build();

        Set<ConstraintViolation<DocumentDto>> violations = validator.validate(documentDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Document number cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void missingDocumentDateTest() {
        DocumentDto documentDto = DocumentDto.builder()
                .documentNumber("P800-901")
                .documentType(DocumentType.SUPPLY.name())
                .supplierName("RTK")
                .build();

        Set<ConstraintViolation<DocumentDto>> violations = validator.validate(documentDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Document date cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void missingSupplierNameTest() {
        DocumentDto documentDto = DocumentDto.builder()
                .documentNumber("P800-901")
                .documentType(DocumentType.SUPPLY.name())
                .documentDate(LocalDate.of(2024, Month.SEPTEMBER, 14))
                .build();

        Set<ConstraintViolation<DocumentDto>> violations = validator.validate(documentDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Supplier name cannot be blank", violations.iterator().next().getMessage());
    }
}
