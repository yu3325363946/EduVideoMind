package it.service.impl;

import it.mapper.VideoMetadataMapper;
import it.pojo.VideoMetadataDto;
import it.service.VideoMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoMetadataServiceImpl implements VideoMetadataService {

    @Autowired
    private VideoMetadataMapper videoMetadataMapper;

    @Override
    public List<VideoMetadataDto> getAllMetadata() {
        List<VideoMetadataDto> list = videoMetadataMapper.selectAllMetadata();
        System.out.println(">>> 查询首页渲染视频数据（最多4条），共 " + list.size() + " 条");
        for (VideoMetadataDto dto : list) {
            System.out.println(">>> 视频ID：" + dto.getVideoId() + "，标题：" + dto.getTitle());
        }
        return list;
    }
}
