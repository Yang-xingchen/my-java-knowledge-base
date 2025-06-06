package com.example.seata.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
@EnableAutoDataSourceProxy(dataSourceProxyMode = "XA")
public class SeataDubboProviderXaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeataDubboProviderXaApplication.class, args);
	}

}
