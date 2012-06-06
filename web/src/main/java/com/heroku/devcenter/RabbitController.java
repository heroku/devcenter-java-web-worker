package com.heroku.devcenter;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
@RequestMapping("/rabbit")
public class RabbitController {

    private final AmqpTemplate rabbit = RabbitUtils.getTemplate();

    @ModelAttribute("bigOp")
    public BigOperation newBigOp() {
        return new BigOperation();
    }

    @RequestMapping()
    public String display() {
        return "rabbitForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String process(@ModelAttribute("bigOp") BigOperation bigOp, Map<String, Object> map) {

        rabbit.convertAndSend(BigOperation.QUEUE_NAME, bigOp);
        System.out.println("Sent message to RabbitMQ: " + bigOp.name);

        return "rabbitConfirmation";
    }
}
