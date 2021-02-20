package com.cheetah.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","你好");
        kafkaProducer.sendMessage("test","在吗");
        try{
            Thread.sleep(1000*10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }

}
@Component
class KafkaConsumer{
    //这样配置之后，springboot就会产生一个线程去一直监听这个主题一旦有消息就会读取，没有就一直阻塞着
    @KafkaListener(topics ={"test"})
    public void handleMessage(ConsumerRecord record){
       System.out.println(record.value());
    }

}