package com.cheetah.community;

import com.cheetah.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Target;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter filter;
    @Test
    public void testSensitiveFilter(){
        String /*text="这里可以赌博，可以嫖娼，可以吸毒，哈哈哈！";
        String string=filter.filter(text);
        System.out.println(string);

        text="这里可以☆赌博，可以嫖☆娼，可以☆吸☆毒，哈哈哈！";
        string=filter.filter(text);
        System.out.println(string);*/

        text="这里是黄色网呀";
        String string=filter.filter(text);
        System.out.println(string);
    }
}
