package com.chw.test.controller;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author CarlBryant
 * @since 2020-04-17
 */
@RestController
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private KafkaProducer<String,String> kafkaProducer;

    @GetMapping("/test/sendMsg")
    public String sendMsg(@RequestParam("msg") String msg){
        kafkaProducer.send(new ProducerRecord<>("lyn","key",msg));
        return "Hello World";
    }


    @RequestMapping("/hello")
    public String hello(Principal principal){
        return "Hello,"+principal.getName();
    }

    @RequestMapping("/failure")
    public String failure(){
        return "登录失败";
    }

    @RequestMapping("/test")
    public String test(){
        return passwordEncoder.encode("123456");
    }

}
