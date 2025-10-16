package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtitleSegment {
    private Long id;
    private Integer videoId;
    private String content;
    private Double startTime;
    private Double endTime;
}