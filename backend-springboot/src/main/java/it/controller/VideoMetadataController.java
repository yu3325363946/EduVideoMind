package it.controller;

import it.pojo.VideoMetadataDto;
import it.service.VideoMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/static")
public class VideoMetadataController {

    @Autowired
    private VideoMetadataService videoMetadataService;

    @GetMapping("/all")
    public List<VideoMetadataDto> getAllMetadata() {
        System.out.println(">>> 接收到请求：GET /api/static/all");
        List<VideoMetadataDto> metadataList = videoMetadataService.getAllMetadata();

        for (VideoMetadataDto dto : metadataList) {
            dto.setCoverUrl("http://localhost:8080" + dto.getCoverUrl());
            System.out.println(">>> 封面地址：" + dto.getCoverUrl());
        }

        return metadataList;
    }
}