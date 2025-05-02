package com.denisenko.inventoryservice.service;

import com.denisenko.inventoryservice.dto.DocumentDto;
import com.denisenko.inventoryservice.exception.PositionNotFoundException;
import com.denisenko.inventoryservice.mapper.DocumentMapper;
import com.denisenko.inventoryservice.model.Document;
import com.denisenko.inventoryservice.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final StringRedisTemplate redisTemplate;
    private final IngredientService ingredientService;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:document:";

    @Transactional
    public DocumentDto createDocument(DocumentDto documentDto, String idempotencyKey) {
        String documentId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (documentId != null) return getDocument(Integer.parseInt(documentId));

        Document document = documentMapper.toEntity(documentDto);
        document.setInventoryMovements(new ArrayList<>(document.getInventoryMovements()));
        documentRepository.save(document);
        ingredientService.updateIngredientPrices(document.getInventoryMovements());
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(document.getId()), Duration.ofMinutes(10));
        return documentMapper.toDTO(document);
    }

    public List<DocumentDto> getAllDocuments() {
        List<Document> documents = documentRepository.findAll();
        return documentMapper.toDTO(documents);
    }

    public DocumentDto getDocument(Integer id) {
        Document document = documentRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Document with ID = " + id + " not found"));
        return documentMapper.toDTO(document);
    }
}
