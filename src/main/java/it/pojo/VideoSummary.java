package it.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoSummary {
    private Long id;
    private Long videoId;
    private Integer chapterIndex;
    private String chapterTitle;
    private String summary;

    public void setCreatedAt(Timestamp timestamp) {
    }
}
