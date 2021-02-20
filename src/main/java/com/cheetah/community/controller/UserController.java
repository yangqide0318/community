package com.cheetah.community.controller;

import com.cheetah.community.annotation.LoginRequired;
import com.cheetah.community.entity.User;
import com.cheetah.community.service.FollowService;
import com.cheetah.community.service.LikeService;
import com.cheetah.community.service.UserService;
import com.cheetah.community.util.CommunityConstant;
import com.cheetah.community.util.CommunityUtil;
import com.cheetah.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String uploadDomain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${qiniu.key.access}")
    private String accessKey;
    @Value("${qiniu.key.secret}")
    private String secretKey;
    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;
    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(Model model){
        //生成上传文件名称
        String fileName=CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy=new StringMap();
        policy.put("returnBody",CommunityUtil.getJSONString(0));
        //生成上传凭证
        Auth auth=Auth.create(accessKey,secretKey);
        String uploadToken=auth.uploadToken(headerBucketName,fileName,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        return "/site/setting";
    }
    //更新头像路径
    @RequestMapping(path = "/header/url",method = RequestMethod.POST)
    @ResponseBody
    public String updataHeaderUrl(String fileName){
        if (StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(1,"文件名不能为空！");
        }
        String url=headerBucketUrl+"/"+fileName;
        userService.updateHeader(hostHolder.getUser().getId(),url);
        return CommunityUtil.getJSONString(0,"上传头像成功");
    }
    //需要重新使用云空间，这个方法将废弃
    //上传头像
    @LoginRequired
    @RequestMapping(path ="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有上传图片");
            return "/site/setting";
        }
        //获取后缀
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确请传入图片");
            return "/site/setting";
        }
        //避免不同用户的图片覆盖,产生一个随机字符这样一来，就算不同用户上传的是相同头像也不会出现覆盖
        fileName = CommunityUtil.generateUUID()+suffix;
        //确定存放路径
        File dest=new File(uploadPath+"/"+fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传失败"+e.getMessage());
            throw new RuntimeException("长传文件失败服务器方式异常",e);
        }
        //更新当前用户头像路径（web访问路径而不是本地访问路径）
        //http://lacalhost:8181/community/user/header/XXX.png
        User user=hostHolder.getUser();
        String headerUrl=uploadDomain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }
    //访问路径也就不能用了
    //获取头像服务,这里的返回值要注意，这个请求向浏览器响应的不是网页也不是字符串，
    // 而是一个二进制数据需要通过流手动写入不能自动注入，前面也有void返回值，那个也是为了展示手动注入
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //找到服务器存放的路径，这里我们的服务器暂时就是我们本电脑，当然后面部署的云服务器或者是虚拟机上路径就会变
        fileName=uploadPath+"/"+ fileName;
        //输出时需要解析文件后缀
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        //java7开始的语法这样写之后在编译时会自动加上finally，并在里面将其关闭
        try(FileInputStream fis=new FileInputStream(fileName);) {
            //输出流是来自response对象所有不用我们管理，但是输入流是自己创建的需要手动关闭
            OutputStream os=response.getOutputStream();
            byte[] buffer=new byte[1024];
            int b=0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }

    //修改密码
    @LoginRequired
    @RequestMapping(path = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String newPassword, String startPassword,String confirmPassword, Model model, @CookieValue("ticket") String ticket){
        if(newPassword!=null){
            model.addAttribute("newPassword",newPassword);
        }
        if(startPassword!=null){
            model.addAttribute("startPassword",startPassword);
        }
        if(confirmPassword!=null){
            model.addAttribute("confirmPassword",confirmPassword);
        }
        if(newPassword==null){
           model.addAttribute("newPasswordMsg","新密码不能为空");
            return "/site/setting";
        }
        if(startPassword==null){

            model.addAttribute("startPasswordMsg","初始密码不能为空");
            return "/site/setting";
        }
        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","两次密码不一致，请重新输入");
            return "/site/setting";
        }
        User user=hostHolder.getUser();
        Map<String,Object> map=userService.updatePassword(user.getId(),newPassword,startPassword,ticket);
        if(map.isEmpty()){
            model.addAttribute("msg","密码修改成功，请重新登录。");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else {
            model.addAttribute("startPasswordMsg", (String) map.get("startPasswordMsg"));
            return "/site/setting";
        }
    }
    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user=userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在!");
        }
        //用户基本信息
        model.addAttribute("user",user);
        //点赞数量
        int likeCount=likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //关注数量
        long followeeCount=followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed=false;
        if(hostHolder.getUser()!=null) {
            hasFollowed=followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";

    }

}
