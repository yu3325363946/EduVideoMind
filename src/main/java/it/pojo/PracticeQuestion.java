package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestion {
    private Long id;                // 主键
    private Long videoId;           // 视频ID
    private Long summaryId;         // 内容段落ID（来自 video_summary）
    private String question;        // 题干
    private String optionA;         // 选项A
    private String optionB;         // 选项B
    private String optionC;         // 选项C
    private String correctAnswer;   // 正确答案（A/B/C）
    private String explanation;     // 题解
}
