## Asynchronous Web-Worker Model Using RabbitMQ in Java

As explained in the [Worker Dynos, Background Jobs and Queueing](background-jobs-queueing) article, web requests
should be completed as fast as possible. If an operation may take a long time, it is best to send it to a worker
dyno to be processed in the background. This article demostrates this with an example application using Spring
[MVC](http://static.springsource.org/spring/docs/current/spring-framework-reference/html/mvc.html) and
[AMPQ](http://www.springsource.org/spring-amqp) with the Heroku [CloudAMPQ add-on](https://addons.heroku.com/cloudamqp),
which provides [RabbitMQ](http://www.rabbitmq.com/) as a service.

### Getting Started

This article walks through an example application pre-configured with the CloudAMPQ add-on.
Follow the steps below to clone the application into your Heroku account:

1. [Verify your Heroku account](https://heroku.com/confirm)
2. [Clone the example reference application](https://api.heroku.com/myapps/devcenter-java-web-worker/clone)
3. Follow instructions in the cloned app to see a demostration and make changes.

The [source code](https://github.com/heroku/devcenter-java-web-worker) of the reference application is also available for browsing or cloning.

If you do not clone the reference app or wish to add CloudAMPQ to another app, use the `heroku addons:add cloudamqp` command:

```sh-session
$ heroku addons:add cloudamqp
Adding cloudamqp to furious-sunrise-1234... done, v14 (free)
cloudamqp documentation available at: https://devcenter.heroku.com/articles/cloudamqp
```

### Application Overview

The application is comprised of two processes: `web` and `worker`.
The `web` process is a simple Spring MVC app that receives requests from users on the web and fowards them as messages to RabbitMQ for background processing.
The `worker` process is a simple Java app using Spring AMPQ that listens for new messages from RabbitMQ and processes them.
The `web` and `worker` processes can be scaled independently depending on application needs.

The application is structured as a Maven multi-module project with `web` and `worker` modules for each of the two
processes as well as a shared `common` module. The `common` module contains the common `BigOperation` model class and the
`RabbitConfiguration` class that reads the `CLOUDAMQP_URL` environment variable provided by the RabbitMQ add-on and
makes it available to the rest of the application:

```java
@Bean
public ConnectionFactory connectionFactory() {
    final URI rabbitMqUrl;
    try {
        rabbitMqUrl = new URI(getEnvOrThrow("CLOUDAMQP_URL"));
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
```

#### Web Process
The `web` process has this configuration `@autowired` by Spring in `BigOperationWebController`:

```java
@Autowired private AmqpTemplate amqpTemplate;
@Autowired private Queue rabbitQueue;
```

When web requests are received by the controller, they are converted to AMPQ messages and sent to RabbitMQ.
The `AmqpTemplate` makes this easy with the following one-liner:

```java
amqpTemplate.convertAndSend(rabbitQueue.getName(), bigOp);
```

The `web` process then immediately returns a confirmation page to the user.

#### Worker Process

Because the `worker` process is running in a separate dyno and is outside an application context,
the configuration must be manually wired from `RabbitConfiguration` in `BigOperationWorker`:

```java
ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
Queue rabbitQueue = rabbitConfig.getBean(Queue.class);
MessageConverter messageConverter = new SimpleMessageConverter();
```

To avoid polling for new messages the `worker` process sets up a `SimpleMessageListenerContainer`, which asynchronously
consumes messages by blocking until a message is delivered. First connection information must be provided:

```java
SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
listenerContainer.setConnectionFactory(rabbitConnectionFactory);
listenerContainer.setQueueNames(rabbitQueue.getName());
```

 Next, the listener is defined by implementing the `MessageListener` interface. This is where the actual message processing happens:

```java
listenerContainer.setMessageListener(new MessageListener() {
    public void onMessage(Message message) {
        // message is converted back into model object
        final BigOperation bigOp = (BigOperation) messageConverter.fromMessage(message);

        // simply printing out the operation, but expensive computation could happen here
        System.out.println("Received from RabbitMQ: " + bigOp);
     }
    });
```

The example application also configures an error handler and shutdown hook for completeness.

Finally the listener container is started, which will stay alive until the JVM is shutdown:

```java
listenerContainer.start();
```
