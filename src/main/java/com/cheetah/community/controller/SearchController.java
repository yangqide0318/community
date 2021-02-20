package com.cheetah.community.controller;

import com.cheetah.community.entity.DiscussPost;
import com.cheetah.community.entity.Page;
import com.cheetah.community.service.ElasticsearchService;
import com.cheetah.community.service.LikeService;
import com.cheetah.community.service.UserService;
import com.cheetah.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    // /search?keyWord
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyWord, Page page, Model model){
        org.springframework.data.domain.Page<DiscussPost> searchResult=
        elasticsearchService.searchDiscussPost(keyWord,page.getCurrent()-1,page.getLimit());
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(searchResult!=null){
            for (DiscussPost post:searchResult){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                //作者
                map.put("user",userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyWord",keyWord);
        //分页信息
        page.setPath("/search?keyWord="+keyWord);
        page.setRows(searchResult==null?0:(int)searchResult.getTotalElements());
        return "site/search";
    }
}
