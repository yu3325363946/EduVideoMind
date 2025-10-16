package it.mapper;

import it.pojo.SubtitleSegment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubtitleSegmentMapper {
    @Insert("INSERT INTO subtitle_segment(video_id, content, start_time, end_time) VALUES(#{videoId}, #{content}, #{startTime}, #{endTime})")
    int insert(SubtitleSegment segment);


    //完整字幕表
    @Select("SELECT id, video_id, content, start_time, end_time FROM subtitle_segment WHERE video_id = #{videoId} ORDER BY start_time")
    List<SubtitleSegment> selectByVideoId(Long videoId);

    //字幕信息
    @Select("SELECT content FROM subtitle_segment WHERE video_id = #{videoId} ORDER BY start_time ASC")
    List<String> getContentByVideoId(@Param("videoId") Long videoId);
}
