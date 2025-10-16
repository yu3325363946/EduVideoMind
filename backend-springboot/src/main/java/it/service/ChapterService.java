package it.service;

import it.pojo.Chapter;
import java.util.List;

public interface ChapterService {
    List<Chapter> generateChapters(Long videoId) throws Exception;
}
