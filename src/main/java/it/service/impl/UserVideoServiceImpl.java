package it.service.impl;

import it.mapper.UserVideoMapper;
import it.pojo.UserVideo;
import it.service.UserVideoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.UUID;

@Service
public class UserVideoServiceImpl implements UserVideoService {

    @Resource
    private UserVideoMapper userVideoMapper;

    private final String saveDir = "D:/upload/user-videos/";

    @Override
    public void upload(MultipartFile file, String title) throws Exception {
        // 保存视频
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File videoFile = new File(saveDir + filename);
        file.transferTo(videoFile);

        // 提取封面图（第一帧）
        String coverName = filename.replace(".mp4", "_cover.jpg");
        String coverPath = saveDir + coverName;

        // 使用 ffmpeg 截取第一帧作为封面
        String cmd = "ffmpeg -y -i " + videoFile.getAbsolutePath() + " -ss 00:00:01 -vframes 1 " + coverPath;
        Runtime.getRuntime().exec(cmd).waitFor();

        // 获取视频时长
        String duration = getVideoDuration(videoFile.getAbsolutePath());

        // 写入数据库
        UserVideo uv = new UserVideo();
        uv.setTitle(title);
        uv.setFilePath(videoFile.getAbsolutePath());
        uv.setCoverUrl(coverPath);
        uv.setDuration(duration);

        userVideoMapper.insert(uv);
    }

    @Override
    public List<UserVideo> list() {
        return userVideoMapper.listAll();
    }

    // 提取视频时长（00:01:35 格式）
    private String getVideoDuration(String path) throws Exception {
        Process process = Runtime.getRuntime().exec(new String[]{
                "ffprobe", "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                path
        });

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        double totalSec = Double.parseDouble(line);
        int min = (int) (totalSec / 60);
        int sec = (int) (totalSec % 60);
        return String.format("00:%02d:%02d", min, sec);
    }
}
