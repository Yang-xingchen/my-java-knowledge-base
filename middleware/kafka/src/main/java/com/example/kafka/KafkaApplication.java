package com.example.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class KafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	@Bean
	public NewTopic testTopic() {
		return TopicBuilder
				.name("test")
				.partitions(10)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic randomTopic() {
		return TopicBuilder
				.name("random")
				.partitions(10)
				.replicas(1)
				.build();
	}

	@Bean
	public CommandLineRunner test(KafkaTemplate<String, String> kafkaTemplate) {
		return args -> {
			kafkaTemplate.send("test", "test1");
		};
	}

	@Bean
	public CommandLineRunner random(KafkaTemplate<String, String> kafkaTemplate) {
		return args -> {
			Random random = new Random();
			while (true) {
				int i = Math.abs(random.nextInt() % 100);
				String message = Integer.toString(i);
				System.out.println("send: " + message);
				kafkaTemplate.send("random", message);
				try {
					TimeUnit.MILLISECONDS.sleep(i * 10 + 500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	@KafkaListener(id = "test", topics = "test")
	public void listener(String data) {
		System.out.println("listener[test]: " + data);
	}

	@KafkaListener(id = "random", topics = "random")
	public void random(List<ConsumerRecord<String, String>> data) {
		System.out.println("listener[random]: " + data);
	}

}
