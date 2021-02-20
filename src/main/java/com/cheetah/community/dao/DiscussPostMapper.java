package com.cheetah.community.dao;

import com.cheetah.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //一语两用
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int orderMode);
    //只有一个参数必须要取别名
    int selectDiscussPostRows(@Param("userId") int userId);
    int insertDiscussPost(DiscussPost discussPost);
    DiscussPost selectDiscussPostById(int id);
    int updateCommentCount(int id,int commentCount);
    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
