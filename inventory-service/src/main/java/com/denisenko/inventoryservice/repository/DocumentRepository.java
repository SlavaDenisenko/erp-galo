package com.denisenko.inventoryservice.repository;

import com.denisenko.inventoryservice.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
