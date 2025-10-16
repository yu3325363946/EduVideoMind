package it.mapper;

import it.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT id, password, username FROM users WHERE id = #{id} AND password = #{password}")
    User login(@Param("id") String id, @Param("password") String password);
}
