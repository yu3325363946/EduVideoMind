package it.service;

import org.springframework.web.multipart.MultipartFile;

public interface AiAssistantService {
    String askQuestion(String sessionId, String question);

    String askByImage(String sessionId, MultipartFile image, String question);
}
