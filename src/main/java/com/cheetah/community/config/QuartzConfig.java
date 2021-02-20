package com.cheetah.community.config;

import com.cheetah.community.quartz.PostScoreRefreshJob;
import com.cheetah.community.quartz.TestJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置——>数据库——>调用,这个配置类就是在初始化的时候起作用，后面实际运行是就是在数据库中去方位相关信息
@Configuration
public class QuartzConfig {
    //BeanFactory和FactoryBean是不一样的
    //FactoryBean可以简化bean实例化过程
    //1.通过FactoryBean封装Bean的是化过程
    //2.将FactoryBean装配到bean容器里
    //3.将FactoryBean注入给其他的Bean
    //4.该bean得到是FactoryBean所管理的对象实例。

    //配置JobDetail
    //@Bean
    public JobDetailFactoryBean testJobDetail() {
        JobDetailFactoryBean factoryBean=new JobDetailFactoryBean();
        factoryBean.setJobClass(TestJob.class);
        factoryBean.setName("testJob");
        factoryBean.setGroup("testJobGroup");
        //是否持久保存，即使任务触发器的没了它还是会存着
        factoryBean.setDurability(true);
        //是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //配置Trigger(SimpleTriggerFactoryBean,CronTriggerFactoryBean,两者前者是简单的，后者功能更为强大)
    //@Bean
    public SimpleTriggerFactoryBean testTrigger(JobDetail testJobDetail){
        SimpleTriggerFactoryBean factoryBean=new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(testJobDetail);
        factoryBean.setName("testTrigger");
        factoryBean.setGroup("testTriggerGroup");
        //执行频率，单位是毫秒
        factoryBean.setRepeatInterval(3000);
        //存储job状态的一个类型
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
    //以上两个对象可能并不是只有一个，当JobDetail有多个时，我们的Trigger在实例化是传入JobDetail优先顺序就要按照方法名对应

    //刷新帖子分数的任务
    //配置JobDetail
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean=new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        //是否持久保存，即使任务触发器的没了它还是会存着
        factoryBean.setDurability(true);
        //是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //配置Trigger(SimpleTriggerFactoryBean,CronTriggerFactoryBean,两者前者是简单的，后者功能更为强大)
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){
        SimpleTriggerFactoryBean factoryBean=new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        //执行频率，单位是毫秒
        factoryBean.setRepeatInterval(1000*60*5);
        //存储job状态的一个类型
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
