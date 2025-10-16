package it.service;

import it.pojo.Video;

public interface BilibiliService {
    Video analyzeFromBvid(String bvid) throws Exception;
}
