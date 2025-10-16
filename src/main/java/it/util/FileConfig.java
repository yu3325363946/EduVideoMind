package it.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {
    private String uploadDir;
    private String processedDir;
    private String frameDir; //截图保存路径
}
