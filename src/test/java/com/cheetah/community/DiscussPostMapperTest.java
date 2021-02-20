package com.cheetah.community;

import com.cheetah.community.dao.DiscussPostMapper;
import com.cheetah.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes =CommunityApplication.class)
public class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void discussPostMapperTest(){
        List<DiscussPost> list=discussPostMapper.selectDiscussPosts(103,0,10,0);
        for(DiscussPost post:list){
            System.out.println(post);
        }
    }
}
