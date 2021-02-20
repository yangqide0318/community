package com.cheetah.community.controller;

import com.cheetah.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/sayHello")
    @ResponseBody
    public String sayHello(){
        return "hello word";
    }
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration=request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name=enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));
        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
        PrintWriter writer=response.getWriter();
        ) {
            writer.write("<h1>牛客网</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //Get请求
    //比如查询所有学生 /student?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@RequestParam(name = "current",required = false,defaultValue = "1") int current, @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current+" "+limit);
        return "some students";
    }
    // /student/123
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a studnet";
    }

    //post请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name+" "+age);
        return "success";
    }

    @RequestMapping(path = "/student",method = RequestMethod.GET)
    @ResponseBody
    public String saveStudent1(String name,int age){
        System.out.println(name+" "+age);
        return "success";
    }
    // 响应HTML数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age",30);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","西华大学");
        model.addAttribute("age",60);
        return "/demo/view";
    }

    //响应json数据,一般在异步请求中使用，当然这里暂时不是异步请求
    //java对象->json字符串->js对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000);
        return emp;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000);
        list.add(emp);
        emp=new HashMap<>();
        emp.put("name","李四");
        emp.put("age",21);
        emp.put("salary",8000);
        list.add(emp);

        return list;
    }
    //cookie示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //设置生效的范围（同一个浏览器多次发送请求，哪些请求才发这个cookie，）
        cookie.setPath("/community/test");
        //设置生存时间，默认是关闭浏览器就没有了，但是设置了这个就会在时间内有效
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);
        return "setCookie";
    }
    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }
    //session示例
    //注意这里返回的包含sessionId的cookie时是不需要我们想上面那样自己去通过HttpServletResponse去传入SpringMVC自己干
    //那之前我们设置的两个参数自然就是默认值，整个项目有效，浏览器一关cookie就不见了
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "session test";

    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    //参数session也是SpringMVC自动注入不用声明
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }
    //ajax示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,String age){
        System.out.println(name+age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }
}
