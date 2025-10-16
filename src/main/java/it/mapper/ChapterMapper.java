package it.mapper;

import it.pojo.Chapter;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChapterMapper {

    @Insert({
        "<script>",
        "INSERT INTO chapter(video_id, chapter_index, title, summary, start_time, end_time, display_time) VALUES",
        "<foreach collection='list' item='item' separator=','>",
        "(",
        "#{item.videoId},",
        "#{item.chapterIndex},",
        "#{item.title},",
        "#{item.summary},",
        "#{item.startTime},",
        "#{item.endTime},",
        "#{item.displayTime}",
        ")",
        "</foreach>",
        "</script>"
    })
    void insertBatch(List<Chapter> list);
}
