package com.cheetah.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;
/*工具类采用静态函数供程序直接使用
* 两个方法一个加密一个生成随机字符串
* */
public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //md5加密,只能加密不能解密，hello->abc123def456但不能倒回
    //还是不安全，应该加上应该随机字符串，在加密，加的字符串叫言
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
    //json字符串的操作方法
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json=new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for (String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code, String msg){
        JSONObject json=new JSONObject();
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        JSONObject json=new JSONObject();
        return getJSONString(code,null,null);
    }
}
