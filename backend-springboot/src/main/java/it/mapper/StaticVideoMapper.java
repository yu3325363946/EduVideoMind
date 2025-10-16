package it.mapper;

import it.pojo.StaticVideo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StaticVideoMapper {

    @Insert("INSERT INTO static_video(title, file_path, cover_url, duration) VALUES(#{title}, #{filePath}, #{coverUrl}, #{duration})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StaticVideo staticVideo);

    @Select("SELECT id, title, cover_url, duration FROM static_video")
    List<StaticVideo> selectAll();

    @Select("SELECT * FROM static_video WHERE id = #{id}")
    StaticVideo selectById(Long id);
}
