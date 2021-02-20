package com.cheetah.community.util;

import com.cheetah.community.entity.User;
import org.springframework.stereotype.Component;
/*
* 持有用户信息，用于代替session(它就是线程隔离的但是我们不想用)对象。
* */
@Component
public class HostHolder {
    //这个对象就是通过线程为key的map来实现的线程隔离
    private ThreadLocal<User> users=new ThreadLocal<>();
    public void setUsers(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }

}
