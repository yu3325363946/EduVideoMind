package it.service;

import it.pojo.SubtitleSegment;
import it.pojo.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface VideoService {
    Video uploadVideo(MultipartFile file, String title) throws Exception;

    Video getVideoById(Long videoId);

    List<SubtitleSegment> getSubtitlesByVideoId(Long videoId);

    Video uploadLocalFile(File file, String title) throws Exception;

}