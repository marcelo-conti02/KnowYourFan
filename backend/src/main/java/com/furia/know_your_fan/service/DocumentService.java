package com.furia.know_your_fan.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.furia.know_your_fan.entity.Document;
import com.furia.know_your_fan.entity.User;
import com.furia.know_your_fan.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserService userService;

    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "documents");

    public Document uploadAndSaveDocument(MultipartFile file, Long userId, String documentType) throws Exception {
        // saves the file
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        if (!Files.exists(UPLOAD_DIR)) {
            Files.createDirectories(UPLOAD_DIR);
        }
        Path filePath = UPLOAD_DIR.resolve(fileName);
        file.transferTo(filePath.toFile());

        // find user
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // create document
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setFilePath(filePath.toString());

        // saves on database
        return documentRepository.save(document);
    }

    public List<Document> findByUserId(Long userId) {
        return documentRepository.findByUserId(userId);
    }
}
