package com.cheetah.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TestInterceptor implements HandlerInterceptor {
    private static final Logger logger= LoggerFactory.getLogger(TestInterceptor.class);
    //在controller请求之前执行，返回值true或者false，代表的是这个方法执行完之后还往不往下执行controller
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandler:"+handler.toString());
        return true;
    }
    //controller之后执行，所有需要返回模板了所有大概率会用到modelAndView
    //Handler参数我们拦截的方法
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandler:"+handler.toString());
    }
    //在程序的最后执行（模板引擎之后）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion"+handler.toString());
    }
}
