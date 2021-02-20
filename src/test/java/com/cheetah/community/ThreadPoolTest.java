package com.cheetah.community;

import com.cheetah.community.service.TestService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {
    private static Logger logger= LoggerFactory.getLogger(ThreadPoolTest.class);
    //jdk普通的线程池
    private ExecutorService executorService= Executors.newFixedThreadPool(5);
    //jdk自带可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService=Executors.newScheduledThreadPool(5);
    //spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    /*@Autowired
    private ThreadPoolTaskExecutor taskExecutor1;*/
    //spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private TestService testService;
    //在main方法下去启动一个线程，因为main是一个主线程,你的启动线程没有完成它就会去等，但是test方法启动的线程就是和我们启动的线程是同级的线程，
    //不会去等待当它自己的程序结束后就会关闭，所有这里我们需要去做一些处理
    private void sleep(long m) {
        try {
            Thread.sleep(m);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    //普通线程池的使用
    @Test
    public void testExecutorService(){
        Runnable task=new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };
        for (int i = 0; i <10 ; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }
    //jdk定时任务线程池
    @Test
    public void testScheduledExecutorService(){
        Runnable task=new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ScheduledExecutorService");
            }
        };
       scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }
    //spring普通线程池
    @Test
    public void testThreadPoolExecutor(){
        Runnable task=new Runnable() {
            @Override
            public void run() {
                logger.debug("hello TreadPoolExecutor");
            }
        };
        for (int i = 0; i <10 ; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }
    //spring定时任务线程池
    @Test
    public void testThreadPoolScheduledExecutor(){
        Runnable task=new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolScheduledExecutor");
            }
        };
        Date startTime=new Date(System.currentTimeMillis()+10000);
        taskScheduler.scheduleAtFixedRate(task,startTime,1000);
        sleep(30000);
    }
    //spring线程池简化调用
    /*@Test
    public void testThreadPoolExecutorSimple(){
        for (int i = 0; i <10 ; i++) {
            testService.execute1();
        }
        sleep(10000);
    }*/
    //spring定时任务线程池简化
    //@Test
    public void testThreadPoolTaskSchedulerSimple(){
        sleep(30000);
    }
}
