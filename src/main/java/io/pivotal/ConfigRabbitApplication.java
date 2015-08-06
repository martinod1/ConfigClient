package io.pivotal;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class ConfigRabbitApplication {
	
	final static String queueName = "arduino-weather-queue";
	
	@Autowired
	AnnotationConfigApplicationContext context;

	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Bean
	Queue queue() {
		return new Queue(queueName, true);
	}
	
	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("arduino-weather");
	}
	
	@Bean
	TopicExchange exchange() {
		return new TopicExchange("arduino-iot-exchange", true, false);
	}
	
    public static void main(String[] args) {
        SpringApplication.run(ConfigRabbitApplication.class, args);
    }
    

    public void run(String... args) throws Exception {
    	
		System.out.println("Sending Message!!");
		rabbitTemplate.convertAndSend(queueName, "sampleID,12.5,34.6");
		System.out.println("Message Sent!!");
		context.close();
	}
    
    @Autowired
    void setEnvironment(Environment e) { //used to test reading of values
    
    	System.out.println(e.getProperty("spring.rabbitmq.host"));
    	System.out.println(e.getProperty("spring.rabbitmq.port"));
    	System.out.println(e.getProperty("spring.rabbitmq.username"));
    	System.out.println(e.getProperty("spring.rabbitmq.password"));	
    }
}

@RestController
@RefreshScope
class ProjectNameRestController {
	
	@Value("${spring.rabbitmq.host}")
	private String rmqHost;
	
	@RequestMapping("/rmq-host")
	String rabbitHost() {
		return this.rmqHost;
	}
	
	@Value("${spring.rabbitmq.port}")
	private String rmqPort;
	
	@RequestMapping("/rmq-port")
	String rabbitPort() {
		return this.rmqPort;
	}
	
	@Value("${spring.rabbitmq.username}")
	private String rmqUsername;
	
	@RequestMapping("/rmq-username")
	String rabbitUsername() {
		return this.rmqUsername;
	}
	
	@Value("${spring.rabbitmq.password}")
	private String rmqPassword;
	
	@RequestMapping("/rmq-password")
	String rabbitPassword() {
		return this.rmqPassword;
	}

	@Value("${configuration.projectName}")
	private String projectName;
	
	@RequestMapping("/project-name")
	String projectName() {
		return this.projectName;
	}
}