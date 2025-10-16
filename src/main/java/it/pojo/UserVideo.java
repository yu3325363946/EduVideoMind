package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVideo {
    private Long id;
    private String title;
    private String filePath;
    private String coverUrl;
    private String duration;
    private String uploadTime;
}
