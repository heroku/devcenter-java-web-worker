package com.heroku.devcenter;

import org.springframework.amqp.core.AmqpTemplate;

public class RabbitReceiver {

    public static void main(String[] args) {
        final AmqpTemplate rabbit = RabbitUtils.getTemplate();

        while (true) {
            System.out.println("Checking for message...");

            try {
                BigOperation bigOp = (BigOperation) rabbit.receiveAndConvert(BigOperation.QUEUE_NAME);
                System.out.println("Received Big Operation: " + bigOp);
            } catch (Exception e) {
                System.err.println("ERROR checking message");
                e.printStackTrace(System.err);
            }
        }
    }
}
