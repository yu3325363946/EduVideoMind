package it.controller;

import it.service.AiAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-assistant")
public class AiAssistantController {

    @Autowired
    private AiAssistantService aiAssistantService;

    //文本入口
    @PostMapping("/text")
    public Map<String, Object> askByText(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        String sessionId = "default";

        String answer = aiAssistantService.askQuestion(sessionId, question);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");

        Map<String, String> data = new HashMap<>();
        data.put("answer", answer);
        response.put("data", data);

        return response;
    }

    //图像分析
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public Map<String, Object> askByImage(
            @RequestPart("image") MultipartFile image,
            @RequestParam(value = "question", required = false) String question) {

        String answer = aiAssistantService.askByImage("default", image, question);

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", Map.of("answer", answer));
        return resp;
    }

}
