package it.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.mapper.SubtitleSegmentMapper;
import it.pojo.VideoNote;
import it.service.VideoNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 生成二级结构化知识文档，支持自动编号与目录生成
 */
@Service
public class VideoNoteServiceImpl implements VideoNoteService {

    @Autowired
    private SubtitleSegmentMapper subtitleSegmentMapper;

    private static final String API_KEY = "sk-af18c52322c04684912d86db0fcb2fe2";
    private static final String API_URL =
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    @Override
    public List<VideoNote> generateNoteByVideoId(Long videoId) {
        System.out.printf(">>> 开始生成结构化笔记，videoId = %d%n", videoId);

        List<String> contents = subtitleSegmentMapper.getContentByVideoId(videoId);
        if (contents.isEmpty()) return Collections.emptyList();

        // ✅ 提示词增强版
        String prompt = String.join("\n",
                "你是教学笔记助手，请根据以下视频字幕生成逻辑清晰的二级结构化笔记，输出 JSON 数组，要求如下：",
                "1. 每项包含 title 和 content 字段。",
                "2. title 的格式为 “一级标题 > 二级标题”（若无子标题，仅写一级标题）。",
                "3. 至少两个章节只包含一级标题（无 > 结构），其它章节包含两个或以上小节（即多个 > 项）。",
                "4. 每段 content 不少于 100 字，教学性强，可适当扩写。",
                "以下为字幕内容：",
                String.join("\n", contents)
        );

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);
        Map<String, Object> body = Map.of("model", "qwen-turbo", "input", Map.of("prompt", prompt));

        String bodyJson;
        try {
            bodyJson = new ObjectMapper().writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("构建请求体失败", e);
        }

        ResponseEntity<String> resp = rest.exchange(API_URL, HttpMethod.POST, new HttpEntity<>(bodyJson, headers), String.class);

        List<VideoNote> notes = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String rawText = mapper.readTree(resp.getBody()).path("output").path("text").asText().trim();

            if (rawText.startsWith("```")) {
                rawText = rawText.substring(rawText.indexOf('\n'), rawText.lastIndexOf("```")).trim();
            }

            JsonNode arr = mapper.readTree(rawText);

            Map<String, Integer> chapterIndexMap = new LinkedHashMap<>();
            Map<String, Integer> subCountMap = new HashMap<>();
            Map<Integer, List<VideoNote>> chapterGroup = new LinkedHashMap<>();

            for (JsonNode item : arr) {
                String rawTitle = item.path("title").asText();
                String content = item.path("content").asText();

                String[] parts = rawTitle.split(" *?> *?", 2);
                String chapterTitle = parts[0].trim();
                String sectionTitle = parts.length > 1 ? parts[1].trim() : null;

                if (!chapterIndexMap.containsKey(chapterTitle)) {
                    chapterIndexMap.put(chapterTitle, chapterIndexMap.size() + 1);
                }
                int chapterIndex = chapterIndexMap.get(chapterTitle);

                Integer subIndex = null;
                if (sectionTitle != null) {
                    int cnt = subCountMap.getOrDefault(chapterTitle, 0) + 1;
                    subCountMap.put(chapterTitle, cnt);
                    subIndex = cnt;
                }

                VideoNote vn = new VideoNote();
                vn.setVideoId(videoId);
                vn.setParentIndex(chapterIndex);
                vn.setNoteIndex(chapterIndex);
                vn.setSubIndex(subIndex);
                vn.setChapterTitle(chapterTitle);
                vn.setTitle(sectionTitle); // ✅ 只保留小节标题，无编号
                vn.setContent(content);

                notes.add(vn);
                chapterGroup.computeIfAbsent(chapterIndex, k -> new ArrayList<>()).add(vn);
            }

        } catch (Exception e) {
            throw new RuntimeException("解析 AI 返回失败", e);
        }

        return notes;
    }

}
