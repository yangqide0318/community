package com.cheetah.community;

import com.cheetah.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testTextMail(){
        mailClient.sendMail("3162426270@qq.com","test","test scuess");
    }
    @Test
    //注意这里的不同，我们可以看到这里同样是产生一个模板页面但是却和controller中的处理时不一样的
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","yqd");
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(context);
        mailClient.sendMail("3162426270@qq.com","html",content);

    }
}
