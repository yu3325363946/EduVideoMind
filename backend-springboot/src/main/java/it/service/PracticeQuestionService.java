package it.service;

import it.pojo.PracticeQuestion;
import it.pojo.UserAnswer;

import java.util.List;

public interface PracticeQuestionService {

    List<PracticeQuestion> generateQuestions(Long videoId);

    List<PracticeQuestion> getQuestions(Long videoId);

    boolean submitAnswer(Long userId, Long questionId, String selectedAnswer);

    List<PracticeQuestion> getWrongQuestions(Long userId, Long videoId);

    PracticeQuestion getQuestionById(Long questionId);

    String getVideoTitleById(Long videoId);


}
