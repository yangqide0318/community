package com.cheetah.community.config;

import com.cheetah.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    /*@Autowired
    private TestInterceptor testInterceptor;*/
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
   /* @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;*/
    @Autowired
    private MessageInterceptor messageInterceptor;
    @Autowired
    private DataInterceptor dataInterceptor;
    //注册接口
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(testInterceptor)
        //        .excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg")
        //      .addPathPatterns("/register", "/login");
        //登录信息显示业务拦截器
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg");
        //登录权限验证拦截器
        //registry.addInterceptor(loginRequiredInterceptor)
                //.excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg");
        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.png","/*/*.jpg","/*/*.jpeg");
    }

}
