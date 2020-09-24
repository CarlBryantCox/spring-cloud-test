package com.chw.test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@MapperScan("com.chw.test.mapper")
@SpringBootApplication
@ServletComponentScan
public class ProjectStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectStartApplication.class,args);
    }

//    @Bean
//    public KafkaProducer<String,String> kafkaProducer(){
//        Properties props = new Properties();
//        props.put("bootstrap.servers", "zookeeper_test:9092,kafka_test:9092,nginx_test:9092");
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("acks","1");
//        props.put("retries",3);
//        props.put("batch.size",323840);
//        props.put("linger.ms",10);
//        props.put("buffer.memory",33554432);
//        props.put("max.block.ms",3000);
//        return new KafkaProducer<>(props);
//    }

}
