package it.service;

import it.pojo.UserVideo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserVideoService {
    void upload(MultipartFile file, String title) throws Exception;
    List<UserVideo> list();
}
