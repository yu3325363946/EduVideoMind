package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaticVideo {
    private Long id;
    private String title;
    private String filePath;
    private String coverUrl;
    private String duration;
}
