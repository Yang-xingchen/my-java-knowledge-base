package com.example.kafka;

import com.alibaba.fastjson2.JSON;
import com.alibaba.otter.canal.protocol.FlatMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }

    @KafkaListener(id = "canal", topics = "canal-topic")
    public void listener(String data) {
        FlatMessage message = JSON.parseObject(data, FlatMessage.class);
        System.out.println(message);
    }

}
