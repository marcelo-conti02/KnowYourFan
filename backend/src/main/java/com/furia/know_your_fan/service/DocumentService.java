package com.furia.know_your_fan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furia.know_your_fan.entity.Document;
import com.furia.know_your_fan.entity.User;
import com.furia.know_your_fan.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserService userService;

    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "documents");
    private static final String OCR_API_KEY = "K85203198988957";
    private static final String OCR_API_URL = "https://api.ocr.space/parse/image";

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

        // validate with OCR and compare CPF
        boolean isValid = validateDocument(filePath.toFile(), user.getCpf());

        // only save document if its validated
        if (!isValid) {
            throw new RuntimeException("Invalid document: CPF does not match the user's.");
        }

        // create document
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setFilePath(filePath.toString());
        document.setValidated(true);

        // saves on database
        return documentRepository.save(document);
    }

    public List<Document> findByUserId(Long userId) {
        return documentRepository.findByUserId(userId);
    }

    // validate document with OCR AI
    private boolean validateDocument(File file, String userCpf) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));
            body.add("language", "por");
            body.add("isOverlayRequired", "false");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("apikey", OCR_API_KEY);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(OCR_API_URL, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                System.out.println("OCR response: " + responseBody);

                // Parse JSON
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);

                JsonNode parsedResults = root.path("ParsedResults");
                if (parsedResults.isArray() && parsedResults.size() > 0) {
                    String parsedText = parsedResults.get(0).path("ParsedText").asText();

                    // extract cpf from text
                    String extractedCpf = extractCpf(parsedText);
                    String cleanUserCpf = userCpf.replaceAll("\\D", "");

                    System.out.println("CPF do usuário: " + cleanUserCpf);
                    System.out.println("CPF extraído: " + extractedCpf);

                    return extractedCpf != null && extractedCpf.equals(cleanUserCpf);

                }
            } else {
                System.err.println("Error on OCR requisition" + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // extract cpf from raw OCR text
    private String extractCpf(String text) {
        Pattern lineWithCpf = Pattern
                .compile("(?i)(CPF[\\s:\\-]*)?(\\d{3}[\\.\\s]?\\d{3}[\\.\\s]?\\d{3}[\\-\\s]?\\d{2})");
        Matcher matcher = lineWithCpf.matcher(text);

        while (matcher.find()) {
            String cpf = matcher.group(2).replaceAll("\\D", "");
            if (cpf.length() == 11)
                return cpf;
        }

        return null;
    }

}
