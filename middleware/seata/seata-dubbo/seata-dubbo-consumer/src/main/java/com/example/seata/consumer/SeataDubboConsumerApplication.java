package com.example.seata.consumer;

import com.example.seata.consumer.at.AtTestService;
import com.example.seata.consumer.tcc.TccTestService;
import com.example.seata.consumer.xa.XaTestService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class SeataDubboConsumerApplication {

	private static final Logger log = LoggerFactory.getLogger(SeataDubboConsumerApplication.class);

	public static void main(String[] args) {
		new SpringApplicationBuilder(SeataDubboConsumerApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}


	private void doCommit(TestService service, String name) {
		Long a = service.create();
		Long b = service.create();
		service.commit(a, b);
		service.check(name + " commit", a, 1, b, 1);
	}

	private void doRollback(TestService service, String name) {
		Long a = service.create();
		Long b = service.create();
		try {
			service.rollback(a, b);
		} catch (Exception e) {
			log.error("{} rollback", name, e);
		}
		service.check(name + " rollback", a, 0, b, 0);
	}

	@Bean
	public CommandLineRunner atCommit(AtTestService testService) {
		return args -> doCommit(testService, "at");
	}

	@Bean
	public CommandLineRunner atRollback(AtTestService testService) {
		return args -> doRollback(testService, "at");
	}

	@Bean
	public CommandLineRunner tccCommit(TccTestService testService) {
		return args -> doCommit(testService, "tcc");
	}

	@Bean
	public CommandLineRunner tccRollback(TccTestService testService) {
		return args -> doRollback(testService, "tcc");
	}

	@Bean
	public CommandLineRunner xaCommit(XaTestService testService) {
		return args -> doCommit(testService, "xa");
	}

	@Bean
	public CommandLineRunner xaRollback(XaTestService testService) {
		return args -> doRollback(testService, "xa");
	}

}
