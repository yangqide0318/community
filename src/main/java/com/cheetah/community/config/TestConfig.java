package com.cheetah.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
/*
* 一个配置类，我们加上一个@Configuration注解就可以实现一个配置类，这个配置类同我们的xml文件一样可以注入Bean
* 这个类的作用就是为spring容器中加入一个bean使用的方法就是使用@Bean注解
* */
/*这里我们可以看到一个@Configuration注解这个注解的作用就是用java类向容器注入Bean，这里面我们来剖析一下这个注解：
*（1）元注解：
*   元注解是一个非常重要的概念我们知道，注解其实就是特殊的一个接口，当然它比接口更加灵活，可以加在类、方法、属性上，普通接口显然不能这样灵活，
*   元注解就是用来注解注解的注解（有点绕口，你细品），常见的元注解有如下几个：
*   ①@Target：用于描述注解的范围，即注解在哪用。取值有很多这里只说一下@Configuration中的这个取值{ElementType.TYPE}
*   ，是用于描述类、接口(包括注解类型) 或enum声明，你可以去踹一踹加在方法上会不会报错
*   ② @Retention：用于描述注解的生命周期，表示需要在什么级别保存该注解，即保留的时间长短。
*   三个时间：SOURCE:在源文件中有效 <- CLASS:在class文件中有效<- RUNTIME:在运行时有效,显然这里肯定是RUNTIME
*   ③@Documented 注解表明这个注解应该被 javadoc工具记录.只是一个标记注解没有成员
*   ④@Inherited用于表示某个被标注的类型是被继承的。如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类。
*（2）@Component：这个注解是一个最基础的扫描注解，把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>，
* 我们的很多注解都是在它的基础上的，比如@Controller@Service
 *  */
@Configuration
public class TestConfig {
    /*
    * @Configuration是告诉spring容器，来来来过来看一下我这里有Bean要给你
    * 这里的@Bean就可以理解为给spring容器具体的Bean而作为@Configuration来说，里面可以有多个Bean
    * */
    @Bean
    /*
    * 这个方法就是为了注入一设置日期的类，有了它就可以让日期格式固定
    * */
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
