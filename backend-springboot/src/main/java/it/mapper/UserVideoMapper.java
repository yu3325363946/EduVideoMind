package it.mapper;

import it.pojo.UserVideo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserVideoMapper {

    @Insert("INSERT INTO user_video(title, file_path, cover_url, duration) " +
            "VALUES(#{title}, #{filePath}, #{coverUrl}, #{duration})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserVideo video);

    @Select("SELECT id, title, cover_url, duration FROM user_video ORDER BY id DESC")
    List<UserVideo> listAll();
}
