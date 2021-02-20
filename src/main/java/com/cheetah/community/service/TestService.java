package com.cheetah.community.service;

import com.cheetah.community.dao.DiscussPostMapper;
import com.cheetah.community.dao.UserMapper;
import com.cheetah.community.entity.DiscussPost;
import com.cheetah.community.entity.User;
import com.cheetah.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class TestService {
    private Logger logger= LoggerFactory.getLogger(TestService.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
    // REQUIRED: 支持当前事务(外部事务),如果不存在则创建新事务.
    // REQUIRES_NEW: 创建一个新事务,并且暂停当前事务(外部事务).
    // NESTED: 如果当前存在事务(外部事务),则嵌套在该事务中执行(独立的提交和回滚),如果不存在则创建新事务.
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user=new User();
        user.setUsername("test");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //新增帖子
        DiscussPost discussPost=new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("test");
        discussPost.setCreateTime(new Date());
        discussPost.setContent("hello");
        discussPostMapper.insertDiscussPost(discussPost);
        Integer.valueOf("abc");
        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //这里需要实现doInTransaction方法，然后transactionTemplate会自己去调用，参数就是通过transactionStatus传入到方法中
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                User user=new User();
                user.setUsername("test");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("test@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                //新增帖子
                DiscussPost discussPost=new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("test");
                discussPost.setCreateTime(new Date());
                discussPost.setContent("hello");
                discussPostMapper.insertDiscussPost(discussPost);
                Integer.valueOf("abc");
                return "ok";
            }
        });

    }
    //spring多线程池的简便运用方法,可以让该方法在多线程的环境下，异步执行
    /*@Async
    public void execute1(){
        logger.debug("execute1");
    }
    @Scheduled(initialDelay = 10000,fixedDelay = 1000)
    public void execute2(){
        logger.debug("execute2");
    }*/
}
