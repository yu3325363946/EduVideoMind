package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {
    private Long id;                // 主键
    private Long userId;
    private Long questionId;
    private String selectedAnswer;  // 用户选择
    private Boolean isCorrect;      // 是否正确


}
