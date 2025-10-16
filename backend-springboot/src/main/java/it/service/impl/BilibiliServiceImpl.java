package it.service.impl;

import it.pojo.Video;
import it.service.BilibiliService;
import it.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Service
public class BilibiliServiceImpl implements BilibiliService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private VideoService videoService;

    @Override
    public Video analyzeFromBvid(String bvid) throws Exception {
        System.out.println("[Bilibili] 接收到 bvid: " + bvid);

        // Step 1: 获取视频信息（cid、title）
        String infoUrl = "https://api.bilibili.com/x/web-interface/view?bvid=" + bvid;
        String infoJson = get(infoUrl);
        System.out.println("[Bilibili] 视频信息响应：" + infoJson);

        String cid = extract(infoJson, "\"cid\":", ",");
        String title = extract(infoJson, "\"title\":\"", "\"");
        System.out.println("[Bilibili] 提取到 cid=" + cid + ", title=" + title);

        // Step 2: 获取视频播放地址
        String playUrl = "https://api.bilibili.com/x/player/playurl?otype=json&fnver=0&fnval=2&player=1&qn=64&bvid=" + bvid + "&cid=" + cid;
        String playJson = get(playUrl);
        System.out.println("[Bilibili] 播放地址响应：" + playJson);

        String videoUrl = extract(playJson, "\"url\":\"", "\"").replace("\\u0026", "&");
        System.out.println("[Bilibili] 视频真实地址：" + videoUrl);

        // Step 3: 下载视频（清洗标题，防止非法字符）
        String safeTitle = sanitizeFilename(title);
        String filename = UUID.randomUUID() + "_" + safeTitle + ".mp4";
        File videoFile = new File(uploadDir, filename);
        download(videoUrl, videoFile);
        System.out.println("[Bilibili] 视频下载完成：" + videoFile.getAbsolutePath());

        // Step 4: 调用已有上传逻辑（解析字幕并存库）
        Video video = videoService.uploadLocalFile(videoFile, title);
        System.out.println("[Bilibili] 上传成功，videoId = " + video.getId());

        return video;
    }

    // 工具方法：GET 请求
    private String get(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000);
        InputStream in = conn.getInputStream();
        return new String(in.readAllBytes());
    }

    // 工具方法：简单提取字段
    private String extract(String text, String prefix, String suffix) {
        int start = text.indexOf(prefix) + prefix.length();
        int end = text.indexOf(suffix, start);
        return text.substring(start, end);
    }

    // 工具方法：清洗非法字符，防止 Windows 文件名错误
    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    // 工具方法：下载文件
    private void download(String urlStr, File dest) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Referer", "https://www.bilibili.com");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(10000);
        InputStream in = conn.getInputStream();
        FileOutputStream out = new FileOutputStream(dest);
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }
}
