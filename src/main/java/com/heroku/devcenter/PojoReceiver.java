package com.heroku.devcenter;

import java.io.IOException;
import java.net.URISyntaxException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.QueueingConsumer;

public class PojoReceiver {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        ConnectionFactory factory = RabbitFactoryUtil.getConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String exchangeName = "sample-exchange";
        String queueName = "sample-queue";
        String routingKey = "sample-key";
        channel.exchangeDeclare(exchangeName, "direct", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);
        
        while (true) {
        	System.out.println("Waiting for message...");
        	//consumer.nextDelivery will block until it receives a message
        	QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery != null) {
                System.out.println("Recieved:->" + new String(delivery.getBody(), "UTF-8"));
            }
        }

    }
}
