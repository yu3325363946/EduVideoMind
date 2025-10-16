package it.service.impl;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.mapper.PracticeQuestionMapper;
import it.mapper.UserAnswerMapper;
import it.mapper.VideoMapper;
import it.mapper.VideoSummaryMapper;
import it.pojo.PracticeQuestion;
import it.pojo.UserAnswer;
import it.pojo.Video;
import it.pojo.VideoSummary;
import it.service.PracticeQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PracticeQuestionServiceImpl implements PracticeQuestionService {

    private static final String API_KEY = "sk-af18c52322c04684912d86db0fcb2fe2";
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    @Autowired
    private PracticeQuestionMapper questionMapper;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private VideoSummaryMapper summaryMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<PracticeQuestion> generateQuestions(Long videoId) {
        System.out.println("正在调用 generateQuestions接口, videoId = " + videoId);
        List<VideoSummary> summaries = summaryMapper.getVideoSummary(videoId);
        List<PracticeQuestion> questions = new ArrayList<>();

        for (int i = 0; i < Math.min(summaries.size(), 12); i++) {
            VideoSummary summary = summaries.get(i);

            // 构造请求
            Map<String, Object> payload = Map.of(
                    "model", "qwen-turbo",
                    "input", Map.of(
                            "prompt",
                            "你是一名计算机专业的老师，" +
                                    "请根据以下内容设计一道具有挑战性且有趣的选择题，题目应包含题干和三个选项（A、B、C），" +
                                    "并标明正确答案和简要题解，格式如下，每项一行：\n" +
                                    "问题：[题干]\n" +
                                    "A. [选项A]\n" +
                                    "B. [选项B]\n" +
                                    "C. [选项C]\n" +
                                    "正确答案：[A/B/C]\n" +
                                    "题解：[简要解释]\n\n" +
                                    "注意：题目不必完全局限于下面内容，可以结合计算机相关知识自由发挥，题目要多样且不重复。\n" +
                                    "内容：" + summary.getSummary()
                    )
            );


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    continue;
                }

                String aiResponse = response.getBody();
                JsonNode root = objectMapper.readTree(aiResponse);
                String content = root.path("output").path("text").asText();

                // 使用正则提取题目内容
                Pattern pattern = Pattern.compile(
                        "问题[:：](.*)\\n" +
                                "A[\\.．、] ?(.*)\\n" +
                                "B[\\.．、] ?(.*)\\n" +
                                "C[\\.．、] ?(.*)\\n" +
                                "正确答案[:：](.*)\\n" +
                                "题解[:：](.*)", Pattern.DOTALL);

                Matcher matcher = pattern.matcher(content);

                if (!matcher.find()) {
                    System.err.println("⚠️ AI 返回格式匹配失败，跳过该题：\n" + content);
                    continue;
                }

                PracticeQuestion pq = new PracticeQuestion();
                pq.setVideoId(videoId);
                pq.setSummaryId(summary.getId());
                pq.setQuestion(matcher.group(1).trim());
                pq.setOptionA(matcher.group(2).trim());
                pq.setOptionB(matcher.group(3).trim());
                pq.setOptionC(matcher.group(4).trim());

                String rawAnswer = matcher.group(5).trim();
                pq.setCorrectAnswer(rawAnswer.length() > 0 ? rawAnswer.substring(0, 1) : "A"); // 默认A防止空串
                pq.setExplanation(matcher.group(6).trim());

                questionMapper.insert(pq);
                questions.add(pq);

            } catch (Exception e) {
                System.err.println("❌ 生成题目失败：" + e.getMessage());
            }
        }

        return questions;
    }
    @Override
    public List<PracticeQuestion> getQuestions(Long videoId) {
        return questionMapper.selectByVideoId(videoId);
    }

    @Override
    public boolean submitAnswer(Long userId, Long questionId, String selectedAnswer) {
        PracticeQuestion q = questionMapper.selectById(questionId);
        boolean correct = q.getCorrectAnswer().equalsIgnoreCase(selectedAnswer);

        UserAnswer ua = new UserAnswer();
        ua.setUserId(userId);
        ua.setQuestionId(questionId);
        ua.setSelectedAnswer(selectedAnswer);
        ua.setIsCorrect(correct);
        userAnswerMapper.insert(ua);

        return correct;
    }

    @Override
    public List<PracticeQuestion> getWrongQuestions(Long userId, Long videoId) {
        return userAnswerMapper.selectWrongAnswers(userId, videoId).stream()
                .map(ua -> questionMapper.selectById(ua.getQuestionId()))
                .toList();
    }

    @Override
    public PracticeQuestion getQuestionById(Long questionId) {
        return questionMapper.selectById(questionId);
    }

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public String getVideoTitleById(Long videoId) {
        Video video = videoMapper.selectById(videoId);
        return video != null ? video.getTitle() : "";
    }

}


