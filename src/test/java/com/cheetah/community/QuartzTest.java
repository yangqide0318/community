package com.cheetah.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QuartzTest {
    //删除一个不用的Quartz Job
    @Autowired
    private Scheduler scheduler;
    @Test
    public void testDeleteJob(){
        try {
            boolean result=scheduler.deleteJob(new JobKey("testJob", "testJobGroup"));
        }catch (SchedulerException e){
            e.printStackTrace();
        }
    }
}
