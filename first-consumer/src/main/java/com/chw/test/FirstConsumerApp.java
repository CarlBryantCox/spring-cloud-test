package com.chw.test;

import com.chw.test.utils.snow.SnowFlake;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableHystrix
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients({"com.chw.test.feign"})
@MapperScan("com.chw.test.mapper")
public class FirstConsumerApp {

    public static void main(String[] args) {
        SpringApplication.run(FirstConsumerApp.class, args);
    }

    @Value("${com.chw.test.dataCenterId}")
    private Long dataCenterId;

    @Value("${com.chw.test.machineId}")
    private Long machineId;

    @Bean
    public SnowFlake snowFlake(){
        return new SnowFlake(dataCenterId,machineId);
    }

}
