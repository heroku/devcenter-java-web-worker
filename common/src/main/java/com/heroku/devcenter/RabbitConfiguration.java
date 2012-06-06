package com.heroku.devcenter;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.System.getenv;

@Configuration
public class RabbitConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        final URI rabbitMqUrl;
        try {
            rabbitMqUrl = new URI(getEnvOrThrow("RABBITMQ_URL"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUsername(rabbitMqUrl.getUserInfo().split(":")[0]);
        factory.setPassword(rabbitMqUrl.getUserInfo().split(":")[1]);
        factory.setHost(rabbitMqUrl.getHost());
        factory.setPort(rabbitMqUrl.getPort());
        factory.setVirtualHost(rabbitMqUrl.getPath().substring(1));

        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue queue() {
        return new Queue("testqueue");
    }

    private static String getEnvOrThrow(String name) {
        final String env = getenv(name);
        if (env == null) {
            throw new IllegalStateException("Environment variable [" + name + "] is not set.");
        }
        return env;
    }

}
