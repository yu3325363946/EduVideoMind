package it.mapper;

import it.pojo.VideoSummary;
import it.pojo.Chapter;
import it.pojo.SubtitleSegment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VideoSummaryMapper {

    @Select("SELECT * FROM chapter WHERE video_id = #{videoId} ORDER BY start_time ASC")
    List<Chapter> getChapters(@Param("videoId") Long videoId);

    @Select("SELECT * FROM subtitle_segment WHERE video_id = #{videoId} ORDER BY start_time ASC")
    List<SubtitleSegment> getSubtitles(@Param("videoId") Long videoId);

    // 注意方法名和Service调用一致
    @Select("SELECT * FROM video_summary WHERE video_id = #{videoId} ORDER BY chapter_index ASC")
    List<VideoSummary> getVideoSummary(@Param("videoId") Long videoId);

    @Delete("DELETE FROM video_summary WHERE video_id = #{videoId}")
    void deleteSummaryByVideoId(@Param("videoId") Long videoId);

    @Insert("INSERT INTO video_summary (video_id, chapter_index, chapter_title, summary, created_at) " +
            "VALUES (#{videoId}, #{chapterIndex}, #{chapterTitle}, #{summary}, #{createdAt})")
    void insertSummary(VideoSummary summary);

}