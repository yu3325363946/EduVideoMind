package it.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 图片映射
        registry.addResourceHandler("/photo/**")
                .addResourceLocations("file:/D:/photo/");

        // 视频映射
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:/D:/upload/videos/");
    }
}
