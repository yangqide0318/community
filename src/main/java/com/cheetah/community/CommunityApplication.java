package com.cheetah.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		//这个操作其实包含了很多东西，它启动了Tomcat服务器，同时它也为我们申请了一个Ioc容器，其实它就是一个配置文件
		SpringApplication.run(CommunityApplication.class, args);
	}

}
