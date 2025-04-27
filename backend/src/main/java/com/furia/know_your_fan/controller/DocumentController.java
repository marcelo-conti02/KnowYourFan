package com.furia.know_your_fan.controller;

import com.furia.know_your_fan.entity.Document;
import com.furia.know_your_fan.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<Document> uploadDocument(@RequestBody Document document) {
        Document savedDocument = documentService.save(document);
        return ResponseEntity.ok(savedDocument);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Document>> getDocumentsByUserId(@PathVariable Long userId) {
        List<Document> documents = documentService.findByUserId(userId);
        return ResponseEntity.ok(documents);
    }
}
