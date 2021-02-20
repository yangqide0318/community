package com.cheetah.community;

import com.cheetah.community.dao.LoginTicketMapper;
import com.cheetah.community.dao.MessageMapper;
import com.cheetah.community.dao.UserMapper;
import com.cheetah.community.entity.LoginTicket;
import com.cheetah.community.entity.Message;
import com.cheetah.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes =CommunityApplication.class)
public class MapperTests {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void testSelectUserById(){
        User user = userMapper.selectById(101);
        System.out.println(user);
    }
    @Test
    public void testInsertUser(){
        User user=new User();
        user.setUsername("yqd");
        user.setPassword("1234546");
        user.setSalt("abc");
        user.setEmail("yqdemail@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows=userMapper.insertUser(user);
        System.out.println(rows);
    }
    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setTicket("xtml");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicketByTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("xtml");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("xtml",1);
        loginTicket=loginTicketMapper.selectByTicket("xtml");
        System.out.println(loginTicket);


    }
    @Test
    public void testMessageMapper(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 10);
        for (Message message:messages) {
            System.out.println(message);
        }
        System.out.println(messageMapper.selectConversationCount(111));
        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message:messages1) {
            System.out.println(message.getConversationId()+message.getContent());
        }
        System.out.println(messageMapper.selectLettersCount("111_112"));
        System.out.println(messageMapper.selectLettersUnreadCount(131,"111_131"));
    }
    @Test
    public void test(){
        Map<String,Object> map=new HashMap<>();
        if(map.isEmpty()){
            System.out.println(map);
        }
    }
}
