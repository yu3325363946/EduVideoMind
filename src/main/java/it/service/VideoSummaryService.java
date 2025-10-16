package it.service;

import it.pojo.VideoSummary;
import java.util.List;

public interface VideoSummaryService {
    List<VideoSummary> generateAndGetSummary(Long videoId);

    List<VideoSummary> getVideoSummary(Long videoId);
}
