package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoMetadataDto {
    private Long videoId;      // 原来叫 id，现在改成 videoId
    private String title;
    private String coverUrl;
    private String duration;
}
