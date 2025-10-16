package it.mapper;

import it.pojo.UserAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAnswerMapper {
    void insert(UserAnswer ua);

    List<UserAnswer> selectWrongAnswers(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId
    );
}

