package it.mapper;

import it.pojo.PracticeQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PracticeQuestionMapper {
    void insert(PracticeQuestion pq);

    PracticeQuestion selectById(@Param("id") Long id);

    List<PracticeQuestion> selectByVideoId(@Param("videoId") Long videoId);
}
