## Using RabbitMQ from Java

RabbitMQ provides a standard Java client. In order to use the client in your project you have to declare the dependency in your build and initialize the connection from the environment variable that Heroku provides to your application.

### Add the RabbitMQ client to Your Pom.xml

Add the following dependency to your pom.xml in order to use the RabbitMQ client:

    <dependency>
      <groupId>com.rabbitmq</groupId>
      <artifactId>amqp-client</artifactId>
      <version>2.7.0</version>
    </dependency>

### Use RabbitMQ in Your Application

Create the RabbitMQ connection factory:

    :::java
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

Send message to a queue:

    :::java
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare("sample-exchange", "direct", true);
    channel.queueDeclare("sample-queue", true, false, false, null);
    channel.queueBind("sample-queue", "sample-exchange", "sample-key");
    channel.basicPublish("sample-exchange", "sample-key", null, "sample message".getBytes("UTF-8");

Receive message from a queue:

    :::java
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare("sample-exchange", "direct", true);
    channel.queueDeclare("sample-queue", true, false, false, null);
    channel.queueBind("sample-queue", "sample-exchange", "sample-key");
    QueueingConsumer consumer = new QueueingConsumer(channel);
    channel.basicConsume(queueName, true, consumer);
        
    while (true) {
        //consumer.nextDelivery will block until it receives a message
       	QueueingConsumer.Delivery delivery = consumer.nextDelivery();
    }

### Using RabbitMQ with Spring

Spring provides a rabbit template that can be used to easily connect to RabbitMQ. These allow much of the setup required each time a queue connection is made to be moved into Spring configuration and kept in the template classes within the Spring container. There are XML namespaces that make the configuration easier as well.

To use the template first declare it in your pom.xml:

    <dependency>
       <groupId>org.springframework.amqp</groupId>
       <artifactId>spring-rabbit</artifactId>
       <version>1.0.0.RELEASE</version>
    </dependency>

Application context XML to configure the rabbit template:

    <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:rabbit="http://www.springframework.org/schema/rabbit"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd
           http://www.springframework.org/schema/rabbit http://www.springframework.org/schema/rabbit/spring-rabbit.xsd"
       default-autowire="byName">

        <context:property-placeholder/>

        <bean id="cf" class="org.springframework.amqp.rabbit.connection.CachingConnectionFactory">
            <!-- This constructor arg utilizes the RabbitFactoryUtil class shown in the java example above -->
            <constructor-arg><value>#{ T(com.heroku.devcenter.RabbitFactoryUtil).getConnectionFactory()}</value></constructor-arg>
        </bean>

        <rabbit:queue id="sample-queue" durable="true" auto-delete="false" exclusive="false" name="sample-queue"/>

        <rabbit:direct-exchange name="sample-exchange" durable="true" auto-delete="false" id="sample-exchange">
            <rabbit:bindings>
                <rabbit:binding queue="sample-queue" key="sample-key"/>
            </rabbit:bindings>
        </rabbit:direct-exchange>

        <bean id="template" class="org.springframework.amqp.rabbit.core.RabbitTemplate">
            <property name="connectionFactory" ref="cf"/>
            <property name="exchange" value="sample-exchange"/>
            <property name="queue" value="sample-queue"/>
            <property name="routingKey" value="sample-key"/>
        </bean>

    </beans>

Sending messages with rabbit template:

    :::java
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    RabbitTemplate rabbitTemplate = ctx.getBean(RabbitTemplate.class);
    rabbitTemplate.send(new Message("sample message".getBytes("UTF-8"), new MessageProperties()));

Receiving messages with rabbit template:

    :::java
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    RabbitTemplate rabbitTemplate = ctx.getBean(RabbitTemplate.class);
    Message response = rabbitTemplate.receive();


You can also download the [sample code](http://github.com/heroku/devcenter-rabbitmq-java)
