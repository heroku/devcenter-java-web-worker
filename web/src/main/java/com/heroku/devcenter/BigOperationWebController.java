package com.heroku.devcenter;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Web controller for receiving BigOperations from users
 * and sending them to RabbitMQ for async processing by BigOperationWorker
 */
@Controller
@RequestMapping("/rabbit")
public class BigOperationWebController {

    // load RabbitMQ configuration provided by Spring
    private final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
    private final AmqpTemplate amqpTemplate = rabbitConfig.getBean(AmqpTemplate.class);
    private final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);

    @ModelAttribute("bigOp")
    public BigOperation newBigOp() {
        return new BigOperation();
    }

    @RequestMapping()
    public String display() {
        return "bigOpSubmissionForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String process(@ModelAttribute("bigOp") BigOperation bigOp, Map<String,Object> map) {
        // Receives the bigOp from the form submission, converts to a message, and sends to RabbitMQ.
        amqpTemplate.convertAndSend(rabbitQueue.getName(), bigOp);
        System.out.println("Sent to RabbitMQ: " + bigOp);

        // Send the bigOp back to the confirmation page for displaying details in view
        map.put("bigOp", bigOp);
        return "bigOpReceivedConfirmation";
    }
}
