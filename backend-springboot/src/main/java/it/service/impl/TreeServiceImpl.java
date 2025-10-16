package it.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.mapper.SubtitleSegmentMapper;
import it.pojo.SubtitleSegment;
import it.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TreeServiceImpl implements TreeService {

    private static final String API_KEY = "sk-af18c52322c04684912d86db0fcb2fe2";
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    @Autowired
    private SubtitleSegmentMapper subtitleSegmentMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> generateTree(Long videoId) {
        System.out.println(">>> 🌲 正在生成知识树，videoId = " + videoId);

        List<SubtitleSegment> subtitles = subtitleSegmentMapper.selectByVideoId(videoId);
        if (subtitles == null || subtitles.isEmpty()) {
            return Map.of("code", 404, "msg", "字幕为空");
        }

        StringBuilder sb = new StringBuilder();
        for (SubtitleSegment s : subtitles) {
            if (s.getContent() != null && !s.getContent().isBlank()) {
                sb.append(s.getContent()).append("\n");
            }
        }
        String text = sb.toString();

        String prompt =
                "你是一名资深的教学内容知识图谱专家。\n\n" +
                        "请根据以下字幕内容，严格提取核心知识点，结构化为树状 JSON 格式：\n\n" +
                        "1. 根节点固定为 \"教学内容\"\n" +
                        "2. 生成且仅生成 **9个子节点**，每个子节点代表一个教学主题\n" +
                        "3. 每个子节点下生成若干知识点（最多5个），表示该主题下的具体内容\n" +
                        "4. 不要生成超过三层结构（根 - 主题 - 知识点）\n" +
                        "5. 内容必须精准、简洁，不要重复或无关\n" +
                        "6. 返回严格遵守以下 JSON 格式：\n\n" +
                        "{\n" +
                        "  \"nodeData\": {\n" +
                        "    \"id\": \"root\",\n" +
                        "    \"topic\": \"教学内容\",\n" +
                        "    \"children\": [\n" +
                        "      {\"id\": \"1\", \"topic\": \"主题1\", \"children\": [\n" +
                        "        {\"id\": \"1-1\", \"topic\": \"知识点1\"},\n" +
                        "        {\"id\": \"1-2\", \"topic\": \"知识点2\"}\n" +
                        "      ]},\n" +
                        "      {\"id\": \"2\", \"topic\": \"主题2\", \"children\": [\n" +
                        "        {\"id\": \"2-1\", \"topic\": \"知识点1\"}\n" +
                        "      ]},\n" +
                        "      ...\n" +
                        "      {\"id\": \"9\", \"topic\": \"主题9\", \"children\": [\n" +
                        "        {\"id\": \"9-1\", \"topic\": \"知识点1\"}\n" +
                        "      ]}\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}\n\n" +
                        "请只返回 JSON，且不要带任何解释文本。\n\n" +
                        "以下是字幕内容，请分析：\n<<<\n" + text + "\n>>>";

        Map<String, Object> payload = Map.of(
                "model", "qwen-turbo",
                "input", Map.of(
                        "prompt", prompt
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return Map.of("code", 500, "msg", "调用 AI 服务失败");
            }

            String responseBody = response.getBody();
            JsonNode aiRoot = objectMapper.readTree(responseBody);
            String content = aiRoot.path("output").path("text").asText();

            JsonNode parsed = objectMapper.readTree(content);
            return Map.of("code", 200, "nodeData", parsed.get("nodeData"));

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("code", 500, "msg", "生成失败：" + e.getMessage());
        }
    }
}
