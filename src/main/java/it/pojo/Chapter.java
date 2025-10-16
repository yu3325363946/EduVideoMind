package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    private Long id;
    private Long videoId;
    private Integer chapterIndex;
    private String title;
    private String summary;
    private Double startTime;
    private Double endTime;

    private String displayTime;
}
