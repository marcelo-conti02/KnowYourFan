package com.furia.know_your_fan.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furia.know_your_fan.entity.Document;
import com.furia.know_your_fan.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    public Document save(Document document) {
        return documentRepository.save(document);
    }

    public List<Document> findByUserId(Long userId) {
        return documentRepository.findByUserId(userId);
    }
}
