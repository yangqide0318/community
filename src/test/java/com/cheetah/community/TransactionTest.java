package com.cheetah.community;

import com.cheetah.community.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTest {
    @Autowired
    private TestService testService;
    @Test
    public void testTransaction1(){
        Object object=testService.save1();
    }
    @Test
    public void testTransaction2(){
        Object object=testService.save2();
    }
}
