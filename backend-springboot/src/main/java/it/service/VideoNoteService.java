package it.service;

import it.pojo.VideoNote;

import java.util.List;

public interface VideoNoteService {
    List<VideoNote> generateNoteByVideoId(Long videoId);
}
