package com.heroku.devcenter;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/rabbit")
public class RabbitController {

    private final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
    private final AmqpTemplate amqpTemplate = rabbitConfig.getBean(AmqpTemplate.class);
    private final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);

    @ModelAttribute("bigOp")
    public BigOperation newBigOp() {
        return new BigOperation();
    }

    @RequestMapping()
    public String display() {
        return "rabbitForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String process(@ModelAttribute("bigOp") BigOperation bigOp) {
        amqpTemplate.convertAndSend(rabbitQueue.getName(), bigOp);
        System.out.println("Sent BigOperation to RabbitMQ on queue: " + bigOp.getName());

        return "rabbitConfirmation";
    }
}
