package it.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.service.AiAssistantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class AiAssistantServiceImpl implements AiAssistantService {



    @Value("${caption.url}")
    private String captionUrl;
    private static final String TEXT_API_URL =
        "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String API_KEY = "sk-af18c52322c04684912d86db0fcb2fe2";

    private final Map<String, List<Map<String, String>>> sessionMap = new HashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String askQuestion(String sessionId, String question) {

        sessionId = "default";
        try {
            List<Map<String, String>> messages =
                sessionMap.computeIfAbsent(sessionId, k -> new ArrayList<>());

            if (messages.isEmpty()) {
                messages.add(Map.of(
                    "role", "system",
                    "content", "你是一位专业的教学教研人员，请回答用户传入的问题，要求不得少于35字，精准且逻辑通顺，不要扩充太多不要答非所问。语气亲切俏皮可以使用emjio"
                ));
            }
            messages.add(Map.of("role", "user", "content", question));

            // 构建请求体
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "qwen-turbo");
            payload.put("input", Map.of("messages", messages));
            payload.put("parameters", Map.of("result_format", "message"));

            // 调用云端文本生成 API
            HttpURLConnection conn = (HttpURLConnection) new URL(TEXT_API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = mapper.writeValueAsString(payload);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int code = conn.getResponseCode();
            InputStream is = (code == 200 ? conn.getInputStream() : conn.getErrorStream());
            JsonNode root = mapper.readTree(is);
            if (code != 200) {
                return "请求失败，状态码：" + code;
            }

            String content = root
                .path("output")
                .path("choices").get(0)
                .path("message")
                .path("content")
                .asText("");

            if (content.isBlank()) {
                return "模型没有返回回答。";
            }

            messages.add(Map.of("role", "assistant", "content", content));
            return content;

        } catch (Exception e) {
            e.printStackTrace();
            return "文本问答出错：" + e.getMessage();
        }
    }

    @Override
    public String askByImage(String sessionId, MultipartFile image, String question) {
        try {
            // 1. 临时保存上传文件
            File tmp = File.createTempFile("upload_img_", Objects.requireNonNull(image.getOriginalFilename()));
            image.transferTo(tmp);

            // 2. 调用本地 BLIP‑2 /caption 服务
            String caption = callCaptionService(tmp);

            // 3. 如果没有 question，只返回描述
            if (!StringUtils.hasText(question)) {
                return "识别到图像内容：\n" + caption;
            }

            // 4. 拼接描述与问题，继续多轮对话
            String prompt = String.format(
                "以下是图片识别到的内容：%s。\n请基于该描述回答问题：%s",
                caption, question
            );
            return askQuestion(sessionId, prompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "图像问答出错：" + e.getMessage();
        }
    }


    private String callCaptionService(File file) throws IOException {
        String boundary = "----Boundary" + System.currentTimeMillis();
        HttpURLConnection conn = (HttpURLConnection) new URL(captionUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setDoOutput(true);

        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream());
             FileInputStream fis = new FileInputStream(file)) {

            out.writeBytes("--" + boundary + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
            out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

            byte[] buf = new byte[4096];
            int len;
            while ((len = fis.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.writeBytes("\r\n--" + boundary + "--\r\n");
            out.flush();
        }

        int code = conn.getResponseCode();
        InputStream is = (code == 200 ? conn.getInputStream() : conn.getErrorStream());
        JsonNode root = mapper.readTree(is);
        return root.path("caption").asText("无法识别图像内容");
    }
}
