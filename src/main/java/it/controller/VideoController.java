package it.controller;

import it.pojo.SubtitleSegment;
import it.pojo.Video;
import it.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/video")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Video video = videoService.uploadVideo(file, title);
            resp.put("code", 200);
            resp.put("msg", "上传成功");
            resp.put("videoId", video.getId());
            resp.put("title", video.getTitle());
        } catch (Exception e) {
            resp.put("code", 500);
            resp.put("msg", "上传失败");
            resp.put("error", e.getMessage());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getVideoDetail(@RequestParam("videoId") Long videoId) {
        Map<String, Object> resp = new HashMap<>();
        Video video = videoService.getVideoById(videoId);
        List<SubtitleSegment> subtitles = videoService.getSubtitlesByVideoId(videoId);

        if (video == null) {
            resp.put("code", 404);
            resp.put("msg", "视频不存在");
            return ResponseEntity.status(404).body(resp);
        }

        List<Map<String, Object>> filteredSubtitles = new ArrayList<>();
        for (SubtitleSegment s : subtitles) {
            Map<String, Object> subMap = new HashMap<>();
            subMap.put("content", s.getContent());
            subMap.put("startTime", s.getStartTime());
            subMap.put("endTime", s.getEndTime());
            filteredSubtitles.add(subMap);
        }

        resp.put("code", 200);
        resp.put("video", video);
        resp.put("subtitles", filteredSubtitles);
        return ResponseEntity.ok(resp);
    }
}