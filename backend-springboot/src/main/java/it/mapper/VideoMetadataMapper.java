package it.mapper;

import it.pojo.VideoMetadataDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface VideoMetadataMapper {
    List<VideoMetadataDto> selectAllMetadata();
}
