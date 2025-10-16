package it.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneVideoMetadata {
    private Long videoId;
    private String title;
    private String coverUrl;
    private String duration;

}
