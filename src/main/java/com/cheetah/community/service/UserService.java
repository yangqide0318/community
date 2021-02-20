package com.cheetah.community.service;

import com.cheetah.community.dao.LoginTicketMapper;
import com.cheetah.community.dao.UserMapper;
import com.cheetah.community.entity.LoginTicket;
import com.cheetah.community.entity.User;
import com.cheetah.community.util.CommunityConstant;
import com.cheetah.community.util.CommunityUtil;
import com.cheetah.community.util.MailClient;
import com.cheetah.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    /*@Autowired
    private LoginTicketMapper loginTicketMapper;*/
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    //用户查询服务
    public User findUserById(int id){
        //return userMapper.selectById(id);
        User user=getCache(id);
        if(user==null){
            user=initCache(id);
        }
        return user;
    }
    //账号注册服务
    public Map<String,Object> register(User user) throws IllegalAccessException {
        Map<String,Object> map=new HashMap<>();
        //对空值进行处理
        if(user==null){
            throw new IllegalAccessException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //判断账号是否存在
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1!=null){
            map.put("usernameMsg","账号已存在");
            return map;
        }
        //验证邮箱
        user1=userMapper.selectByEmail(user.getEmail());
        if(user1!=null){
            map.put("emailMsg","邮箱已注册");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //发生激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }
    //激活码准确性验证
    public int activation(int userId,String code){
        User user=userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }
    //查询ticket
    public LoginTicket findLoginTicket(String ticket){
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }
    //更新头像地址
    public int updateHeader(int userId,String headerUrl){
        int rows=userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;

    }
    //修改用户密码
    public Map<String, Object> updatePassword(int userId, String newPassword, String startPassword,String ticket){
        Map<String,Object> map=new HashMap<>();
        User user = userMapper.selectById(userId);
        startPassword= CommunityUtil.md5(startPassword+user.getSalt());
        if(!user.getPassword().equals(startPassword)){
            map.put("startPasswordMsg","初始密码错误，请重新登录或者找回密码");
            return map;
        }else{
            userMapper.updatePassword(userId,CommunityUtil.md5(newPassword+user.getSalt()));
            clearCache(userId);
            String redisKey= RedisKeyUtil.getTicketKey(ticket);
            LoginTicket loginTicket=(LoginTicket) redisTemplate.opsForValue().get(redisKey);
            loginTicket.setStatus(1);
            redisTemplate.opsForValue().set(redisKey,loginTicket);
            return map;
        }

    }
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
    //缓存三件事
    //1.优先在缓存中取数据
    private User getCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.取不到是初始化缓存数据
    private User initCache(int userId){
        User user=userMapper.selectById(userId);
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据变更时清除缓存数据
    private void clearCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
    //获得用户权限的方法
    public Collection<? extends GrantedAuthority> getAuthority(int userId){
        User user=this.findUserById(userId);
        List<GrantedAuthority> list=new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
