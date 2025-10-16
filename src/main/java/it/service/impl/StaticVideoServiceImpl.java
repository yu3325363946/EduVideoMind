package it.service.impl;

import it.mapper.StaticVideoMapper;
import it.pojo.StaticVideo;
import it.service.StaticVideoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaticVideoServiceImpl implements StaticVideoService {

    private final StaticVideoMapper staticVideoMapper;

    public StaticVideoServiceImpl(StaticVideoMapper staticVideoMapper) {
        this.staticVideoMapper = staticVideoMapper;
    }

    @Override
    public List<StaticVideo> getAllStaticVideos() {
        return staticVideoMapper.selectAll();
    }
}
