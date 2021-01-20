package com.cheetah.community;

import com.cheetah.community.dao.UserMapper;
import com.cheetah.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes =CommunityApplication.class)
public class MapperTests {
    @Resource
    private UserMapper userMapper;
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
}
