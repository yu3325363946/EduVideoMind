package it.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import it.mapper.SubtitleSegmentMapper;
import it.mapper.VideoMapper;
import it.pojo.SubtitleSegment;
import it.pojo.Video;
import it.service.VideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${whisper.url}")
    private String whisperUrl;


    private static final String VIDEO_ACCESS_PREFIX = "http://localhost:8080/videos/";

    private final VideoMapper videoMapper;
    private final SubtitleSegmentMapper subtitleSegmentMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VideoServiceImpl(VideoMapper videoMapper, SubtitleSegmentMapper subtitleSegmentMapper) {
        this.videoMapper = videoMapper;
        this.subtitleSegmentMapper = subtitleSegmentMapper;
    }

    @PostConstruct
    public void logConfig() {
        System.out.println(">>> whisper.url = " + whisperUrl);
        System.out.println(">>> uploadDir = " + uploadDir);
    }

    @Override
    public Video uploadVideo(MultipartFile file, String title) throws Exception {
        System.out.println("上传视频开始，文件名：" + file.getOriginalFilename() + "，标题：" + title);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, filename);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        System.out.println("视频保存路径：" + dest.getAbsolutePath());

        Video video = new Video();
        video.setTitle(title);
        video.setFilePath("http://localhost:8080/videos/" + filename);
        videoMapper.insert(video);
        System.out.println("视频入库成功，ID=" + video.getId());

        System.out.println("调用 Whisper，URL=" + whisperUrl);
        List<SubtitleSegment> subtitles = callWhisper(dest, Math.toIntExact(video.getId()));
        System.out.println("Whisper 返回字幕段数量：" + subtitles.size());

        for (SubtitleSegment s : subtitles) {
            subtitleSegmentMapper.insert(s);
        }
        System.out.println("字幕入库完成");

        return video;
    }

    @Override
    public Video getVideoById(Long videoId) {
        return videoMapper.selectById(videoId);
    }

    @Override
    public List<SubtitleSegment> getSubtitlesByVideoId(Long videoId) {
        return subtitleSegmentMapper.selectByVideoId(videoId);
    }

    private List<SubtitleSegment> callWhisper(File videoFile, Integer videoId) throws Exception {
        System.out.println("调用 Whisper，URL=" + whisperUrl);

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(videoFile));
        body.add("language", "zh");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(whisperUrl, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Whisper调用失败，状态码：" + response.getStatusCode().value() + "，响应：" + response.getBody());
        }

        JsonNode root = objectMapper.readTree(response.getBody());

        List<SubtitleSegment> result = new ArrayList<>();
        if (root.has("segments")) {
            for (JsonNode seg : root.get("segments")) {
                String text = seg.get("text").asText();
                String simplifiedText = ZhConverterUtil.toSimple(text);

                SubtitleSegment s = new SubtitleSegment();
                s.setVideoId(videoId);
                s.setContent(simplifiedText);
                s.setStartTime(seg.get("start").asDouble());
                s.setEndTime(seg.get("end").asDouble());
                result.add(s);
            }
        } else {
            String text = root.path("text").asText("");
            String simplifiedText = ZhConverterUtil.toSimple(text);

            SubtitleSegment s = new SubtitleSegment();
            s.setVideoId(videoId);
            s.setContent(simplifiedText);
            s.setStartTime(0.0);
            s.setEndTime(0.0);
            result.add(s);
        }
        return result;
    }
    @Override
    public Video uploadLocalFile(File file, String title) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile(
                    file.getName(),
                    file.getName(),
                    "video/mp4",
                    fis
            );
            return uploadVideo(multipartFile, title);
        }
    }
}
