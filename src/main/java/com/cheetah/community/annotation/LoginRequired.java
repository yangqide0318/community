package com.cheetah.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//这个注解只是起到一个标识作用代表是一个登陆才能访问的方法
public @interface LoginRequired {
}
