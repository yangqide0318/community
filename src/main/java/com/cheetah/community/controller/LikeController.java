package com.cheetah.community.controller;

import com.cheetah.community.entity.Comment;
import com.cheetah.community.entity.DiscussPost;
import com.cheetah.community.entity.Event;
import com.cheetah.community.entity.User;
import com.cheetah.community.event.EventProducer;
import com.cheetah.community.service.LikeService;
import com.cheetah.community.util.CommunityConstant;
import com.cheetah.community.util.CommunityUtil;
import com.cheetah.community.util.HostHolder;
import com.cheetah.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user=hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //获取点赞数
        long likeCount=likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus=likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        //封装信息传入页面
        Map<String,Object> map=new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        if(likeStatus==1){
            //触发点赞事件
            Event event=new Event().setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }
        if(entityType==ENTITY_TYPE_POST){
            String redisKey= RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }
        return CommunityUtil.getJSONString(0,null,map);
    }
}
