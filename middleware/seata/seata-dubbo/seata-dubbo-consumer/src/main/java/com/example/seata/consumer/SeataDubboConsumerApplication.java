package com.example.seata.consumer;

import com.example.seata.consumer.at.AtTestService;
import com.example.seata.consumer.tcc.TccTestService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
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

	public static void main(String[] args) {
		new SpringApplicationBuilder(SeataDubboConsumerApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Bean
	public CommandLineRunner atCommit(AtTestService atTestService) {
		return args -> {
			Long id = atTestService.commit();
			atTestService.check("at commit", id, 2);
		};
	}

	@Bean
	public CommandLineRunner atRollback(AtTestService atTestService) {
		return args -> {
			Long id;
			try {
				id = atTestService.rollback();
			} catch (TestException e) {
				id = e.getId();
			}
			atTestService.check("at rollback", id, null);
		};
	}

	@Bean
	public CommandLineRunner tccCommit(TccTestService tccTestService) {
		return args -> {
			Long id = tccTestService.commit();
			tccTestService.check("tcc commit", id, 2);
		};
	}

	@Bean
	public CommandLineRunner tccRollback(TccTestService tccTestService) {
		return args -> {
			Long id;
			try {
				id = tccTestService.rollback();
			} catch (TestException e) {
				id = e.getId();
			}
			tccTestService.check("tcc rollback", id, null);
		};
	}

}
