package com.cheetah.community.dao;

import com.cheetah.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //一语两用
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);
    //只有一个参数必须要取别名
    int selectDiscussPostRows(@Param("userId") int userId);

}
