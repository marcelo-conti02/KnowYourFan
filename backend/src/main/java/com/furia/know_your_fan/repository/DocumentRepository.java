package com.furia.know_your_fan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.furia.know_your_fan.entity.Document;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
}
