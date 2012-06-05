package com.heroku.devcenter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/pdf")
public class RabbitController {

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        RabbitTemplate rabbitTemplate = ctx.getBean(RabbitTemplate.class);
        while(true){
            String msg = "Spring Sent at:" + System.currentTimeMillis();
            System.out.println(msg);
            byte[] body = msg.getBytes("UTF-8");
            rabbitTemplate.send(new Message(body, new MessageProperties()));
            Thread.sleep(1000);
        }
    }
}
