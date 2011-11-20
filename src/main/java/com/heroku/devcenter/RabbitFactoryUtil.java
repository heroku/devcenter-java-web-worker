package com.heroku.devcenter;

import static java.lang.System.getenv;
import java.net.URI;
import java.net.URISyntaxException;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitFactoryUtil {

    public static ConnectionFactory getConnectionFactory() throws URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();

        URI uri = new URI(getenv("RABBITMQ_URL"));
        factory.setUsername(uri.getUserInfo().split(":")[0]);
        factory.setPassword(uri.getUserInfo().split(":")[1]);
        factory.setHost(uri.getHost());
        factory.setPort(uri.getPort());
        factory.setVirtualHost(uri.getPath().substring(1));

        return factory;
    }

}
