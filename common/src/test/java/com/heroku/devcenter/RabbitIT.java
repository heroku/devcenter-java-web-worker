package com.heroku.devcenter;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ErrorHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Ryan Brainard
 */
public class RabbitIT {

    private final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
    private final ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
    private final AmqpTemplate amqpTemplate = rabbitConfig.getBean(AmqpTemplate.class);
    private final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);

    @BeforeTest
    public void cleanTheRabbit() {
        while (amqpTemplate.receive(rabbitQueue.getName()) != null){}
    }

    @Test
    public void testSynchronous() throws Exception {
        amqpTemplate.convertAndSend(rabbitQueue.getName(), new BigOperation("foo"));
        Assert.assertEquals(((BigOperation) amqpTemplate.receiveAndConvert(rabbitQueue.getName())).getName(), "foo");
    }

    @Test
    public void testAsynchronous() throws Exception {
        final MessageConverter messageConverter = new SimpleMessageConverter();
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory);
        container.setQueueNames(rabbitQueue.getName());

        final CountDownLatch fooLatch = new CountDownLatch(1);
        final CountDownLatch barLatch = new CountDownLatch(2);
        final List<BigOperation> receievedMessageHolder = new ArrayList<BigOperation>(2);
        container.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                receievedMessageHolder.add((BigOperation) messageConverter.fromMessage(message));
                fooLatch.countDown();
                barLatch.countDown();
            }
        });
        container.setErrorHandler(new ErrorHandler() {
            public void handleError(Throwable t) {
                t.printStackTrace();
            }
        });

        try {
            container.start();

            amqpTemplate.convertAndSend(rabbitQueue.getName(), new BigOperation("foo"));
            assertTrue(fooLatch.await(5, TimeUnit.SECONDS));
            assertEquals(receievedMessageHolder.get(0).getName(), "foo");

            amqpTemplate.convertAndSend(rabbitQueue.getName(), new BigOperation("bar"));
            assertTrue(barLatch.await(5, TimeUnit.SECONDS));
            assertEquals(receievedMessageHolder.get(1).getName(), "bar");
        } finally {
            container.shutdown();
        }
    }

}
