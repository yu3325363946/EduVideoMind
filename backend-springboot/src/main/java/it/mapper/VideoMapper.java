package it.mapper;

import it.pojo.Video;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VideoMapper {
    @Insert("INSERT INTO video(title, file_path) VALUES(#{title}, #{filePath})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Video video);

    @Select("SELECT id, title, file_path FROM video WHERE id = #{id}")
    Video selectById(Long id);

    @Select("SELECT id, title, file_path FROM video")
    List<Video> selectAll();
}
