package com.heroku.devcenter;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.System.getenv;

public class RabbitUtils {

    private RabbitUtils(){}

    public static AmqpTemplate getTemplate() {
        ApplicationContext context = new GenericXmlApplicationContext("classpath:/rabbit-context.xml");
        return context.getBean(AmqpTemplate.class);
    }

    public static ConnectionFactory getConnectionFactory() throws URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();

        URI uri = new URI(getEnvOrThrow("RABBITMQ_URL"));
        factory.setUsername(uri.getUserInfo().split(":")[0]);
        factory.setPassword(uri.getUserInfo().split(":")[1]);
        factory.setHost(uri.getHost());
        factory.setPort(uri.getPort());
        factory.setVirtualHost(uri.getPath().substring(1));

        return factory;
    }
    
    private static String getEnvOrThrow(String name) {
        final String env = getenv(name);
        if (env == null) {
            throw new IllegalStateException("Environment variable [" + name + "] is not set.");
        }
        return env;
    }

}
