package it.controller;

import it.pojo.PracticeQuestion;
import it.service.PracticeQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/practice")
public class PracticeQuestionController {

    @Autowired
    private PracticeQuestionService practiceQuestionService;


    //生成
    @PostMapping("/generate")
    public Map<String, Object> generateQuestions(@RequestParam Long videoId) {
        System.out.println(">>> 收到前端请求：生成题目，videoId = " + videoId);
        List<PracticeQuestion> questions = practiceQuestionService.generateQuestions(videoId);
        boolean success = questions != null && !questions.isEmpty();

        String videoTitle = practiceQuestionService.getVideoTitleById(videoId); // 👈 新增

        return Map.of(
                "videoId", videoId,
                "videoTitle", videoTitle,
                "generated", success,
                "count", questions.size()
        );
    }


    //渲染
    @GetMapping("/list")
    public Map<String, Object> getQuestions(@RequestParam Long videoId) {
        System.out.println(">>> 收到前端请求：开始渲染，videoId = " + videoId);
        List<PracticeQuestion> questions = practiceQuestionService.getQuestions(videoId);

        // 查询视频标题
        String videoTitle = practiceQuestionService.getVideoTitleById(videoId);;

        // 构造题目列表（不包含答案和题解）
        List<Map<String, Object>> questionList = new ArrayList<>();
        for (PracticeQuestion q : questions) {
            Map<String, Object> item = new HashMap<>();
            item.put("questionId", q.getId());
            item.put("question", q.getQuestion());
            item.put("optionA", q.getOptionA());
            item.put("optionB", q.getOptionB());
            item.put("optionC", q.getOptionC());
            questionList.add(item);
        }

        return Map.of(
                "videoId", videoId,
                "videoTitle", videoTitle,
                "questions", questionList
        );
    }



    //提交答案的接口
    @PostMapping("/submitAll")
    public List<Map<String, Object>> submitAllAnswers(
            @RequestBody List<Map<String, Object>> answers
    ) {
        List<Map<String, Object>> results = new ArrayList<>();

        // 遍历所有提交的答案
        for (Map<String, Object> ans : answers) {
            Long questionId = Long.valueOf(ans.get("questionId").toString());
            String selectedAnswer = ans.get("selectedAnswer").toString();

            // 判断是否正确
            PracticeQuestion q = practiceQuestionService.getQuestionById(questionId);
            if (q == null) continue;

            boolean correct = q.getCorrectAnswer().equalsIgnoreCase(selectedAnswer);

            Map<String, Object> res = new HashMap<>();
            res.put("question", q.getQuestion());
            res.put("correct", correct);
            res.put("yourAnswer", selectedAnswer);
            res.put("correctAnswer", q.getCorrectAnswer());
            res.put("explanation", q.getExplanation());
            results.add(res);
        }

        return results;
    }
}