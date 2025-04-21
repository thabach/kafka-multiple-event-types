package io.doubledispatch.kafka.listener_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.event.KafkaEvent;

@SpringBootApplication
public class SpringKafkaStagedClientApplication implements ApplicationListener<KafkaEvent> {

	private final static Logger logger = LoggerFactory.getLogger(SpringKafkaStagedClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringKafkaStagedClientApplication.class, args);
	}

	@Override
	public void onApplicationEvent(KafkaEvent event) {
		logger.info("#### Spring Kafka ApplicationEvent ##### " + event);
	}

}
