package com.cheetah.community.service;

import com.cheetah.community.dao.MessageMapper;
import com.cheetah.community.entity.Message;
import com.cheetah.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLettersCount(String conversationId){
        return messageMapper.selectLettersCount(conversationId);
    }

    public int findLettersUnreadCount(int userId,String conversationId){
        return messageMapper.selectLettersUnreadCount(userId, conversationId);
    }
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }

    public Message findLatestNotice(int userId,String topic){
        return messageMapper.selectLatestNotice(userId, topic);
    }
    public int findNoticeCount(int userId,String topic){
        return  messageMapper.selectNoticeCount(userId, topic);
    }
    public int findNoticeUnreadCount(int userId,String topic){
        return  messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    public List<Message> findNotices(int id, String conversationId, int offset, int limit) {
        return messageMapper.selectNotices(id,conversationId,offset,limit);
    }
}
