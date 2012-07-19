## Asynchronous Web-Worker Model Using RabbitMQ in Java

Long running web requests should be processed asynchronously through a worker process as a the best practice. For a more in-depth understanding of this architectural pattern read the [Worker Dynos, Background Jobs and Queueing](https://devcenter.heroku.com/articles/background-jobs-queueing) article.

The article demonstrates this pattern using an sample Java application with [Spring MVC](http://static.springsource.org/spring/docs/current/spring-framework-reference/html/mvc.html) and [RabbitMQ](http://www.rabbitmq.com/). It leverages [CloudAMPQ add-on](https://addons.heroku.com/cloudamqp) which is one of the RabbitMQ addons in the Heroku add-ons catalog.

### Getting Started

Follow the below steps below to clone this application into your Heroku account:

1. [Clone](https://api.heroku.com/myapps/devcenter-java-web-worker/clone) this sample application into your Heroku account
2. Go to `http://yourappname.herokuapp.com/spring/bigOp` to try out the application
3. Following the instructions at `http://yourappname.herokuapp.com/` to make changes using Eclipse or command line

Sample code of [this application](https://github.com/heroku/devcenter-java-web-worker) is available on GitHub.

### Application Overview

The application is comprised of two processes: `web` and `worker`.

* __`web`__ : A simple Spring MVC app that receives web requests and queues them in RabbitMQ for processing.
* __`worker`__ :  A standalone Java application using Spring AMQP to read & processes messages from RabbitMQ.

Because these are separate processes, they can be scaled independently based on specific application needs. Read the [Process Model](https://devcenter.heroku.com/articles/process-model) article for a more in-depth understanding of Heroku's process model.

### RabbitMQ Configuration

The RabbitMQ configuration is done through `RabbitConfiguration.java` which reads the `CLOUDAMQP_URL` environment variable provided by the [CloudAMPQ](https://addons.heroku.com/cloudamqp) add-on, and makes it available to the rest of the application.

     :::java
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

### Web Process

`BigOperationWebController.java`, a Spring MVC controller queues up the web requests into RabbitMQ. This class has the the Spring AMQP Template and queue configuration `@autowired`.

    :::java
    @Autowired private AmqpTemplate amqpTemplate;
    @Autowired private Queue rabbitQueue;

When web requests are received by the controller, they are coverted to AMPQ messages and sent to RabbitMQ. The `AmqpTemplate` makes this easy by including the following line:

    :::java
    amqpTemplate.convertAndSend(rabbitQueue.getName(), bigOp);

The `web` process immediately returns a confirmation page to the user.

### Worker Process

The `worker` process is running as a separate process outside of the Spring web application context. Hence the configuration must be explicitly wired from `RabbitConfiguration`. `BigOperationWorker` is the main Java class executed in the `worker` processes and loads the RabbitMQ configuration as below:

    :::java
    ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
    ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
    Queue rabbitQueue = rabbitConfig.getBean(Queue.class);
    MessageConverter messageConverter = new SimpleMessageConverter();

Spring provides a convenience class `SimpleMessageListenerContainer` to receive messages from a queue and delegate it to the MessageListener that is injected into it. The RabbitMQ connection for `SimpleMessageListenerContainer` is setup as follows:

    :::java
    SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
    listenerContainer.setConnectionFactory(rabbitConnectionFactory);
    listenerContainer.setQueueNames(rabbitQueue.getName());

 The listener is defined by implementing the `MessageListener` interface. The long running `BigOperation` is invoked from the listener.

    :::java
     listenerContainer.setMessageListener(new MessageListener() {
             public void onMessage(Message message) {
                 // message is converted back into model object
                 final BigOperation bigOp = (BigOperation) messageConverter.fromMessage(message);
                
                 // simply printing out the operation, but expensive computation would happen here
                 System.out.println("Received from RabbitMQ: " + bigOp);
             }
         });


To start listening for messages on the queue, the listener container needs to be started.

    :::java
    listenerContainer.start();

### Maven project setup

The application is structured as a Maven multi-module project with 3 modules: web, worker (for each of the two processes) and common module which contains the `BigOperation.java` model class and the `RabbitConfiguration.java` configuration class. 
