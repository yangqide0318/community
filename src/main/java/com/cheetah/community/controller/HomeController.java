package com.cheetah.community.controller;

import com.cheetah.community.entity.DiscussPost;
import com.cheetah.community.entity.Page;
import com.cheetah.community.entity.User;
import com.cheetah.community.service.DiscussPostService;
import com.cheetah.community.service.LikeService;
import com.cheetah.community.service.UserService;
import com.cheetah.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,@RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        //方法调用之前springMVC他会自动实例化Model和Page，并将Page注入给Model，
        //所以在模板中就可以直接访问Page中的数据，而不用自己加入到Model中
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list=discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(list!=null){
            for (DiscussPost post:list){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user=userService.findUserById(post.getUserId());
                map.put("user",user);
                long likeCount=likeService.findEntityLikeCount(ACTIVATION_REPEAT,post.getId());
                map.put("likeCount",likeCount);
                //查询点赞数量
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    //拒绝访问时的页面
    @RequestMapping(path = "/denied", method =RequestMethod.GET)
    public String getDenied() {
        return "/error/404";
    }
}
