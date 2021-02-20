package com.cheetah.community.dao;

import com.cheetah.community.entity.Comment;
import com.cheetah.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //针对每个会话只返回一个最新的私信
    List<Message> selectConversations(int userId,int offset,int limit);
    //查询当前用户数量
    int selectConversationCount(int userId);
    //查询会话的全部私信
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //某个会话的私信数量
    int selectLettersCount(String conversationId);
    //查询未读私信的数量
    int selectLettersUnreadCount(int userId,String conversationId);
    //新增消息
    int insertMessage(Message message);
    //更改消息状态
    int updateStatus(List<Integer> ids,int status);
    //查询某个主题下的最新通知
    Message selectLatestNotice(int userId,String topic);
    //查询某个主题的通知的数量
    int selectNoticeCount(int userId,String topic);
    //查询某个主题的未读通知的数量
    int selectNoticeUnreadCount(int userId,String topic);
    //查询通知的详情列表
    List<Message> selectNotices(int userId, String conversationId, int offset, int limit);
}
