package com.heroku.devcenter;

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

public class RabbitReceiver {

    public static void main(String[] args) {
        final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        final ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
        final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);
        final MessageConverter messageConverter = new SimpleMessageConverter();

        // create a listener container, which is required for asynchronous message consumption.
        // AmqpTemplate cannot be used in this case
        final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        listenerContainer.setQueueNames(rabbitQueue.getName());

        // set the callback for message handling
        listenerContainer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                final BigOperation bigOp = (BigOperation) messageConverter.fromMessage(message);
                System.out.println("Received Big Operation: " + bigOp.getName());
            }
        });

        // set a simple error handler
        listenerContainer.setErrorHandler(new ErrorHandler() {
            public void handleError(Throwable t) {
                t.printStackTrace();
            }
        });

        // register a shutdown hook with the JVM
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down RabbitReceiver");
                listenerContainer.shutdown();
            }
        });

        // start up the listener. this will block until JVM is killed.
        listenerContainer.start();
    }
}
