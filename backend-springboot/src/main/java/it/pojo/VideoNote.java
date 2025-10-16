package it.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoNote {
    private Long id;
    private Long videoId;
    private Integer noteIndex;
    private Integer parentIndex;
    private Integer subIndex;
    private String chapterTitle;
    private String title;
    private String content;
}
