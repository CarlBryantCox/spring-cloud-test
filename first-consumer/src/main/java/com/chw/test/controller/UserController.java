package com.chw.test.controller;


import com.chw.test.feign.TestFeign;
import com.chw.test.utils.snow.SnowFlake;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author CarlBryant
 * @since 2020-06-03
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TestFeign testFeign;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private SnowFlake snowFlake;

    @HystrixCommand(fallbackMethod = "getBackMessage")
    @GetMapping("/testFirst")
    public String testFirst(){
        System.out.println("请求调用消费者");
        return testFeign.testOne();
    }

    public String getBackMessage(){
        return "超时，返回默认消息";
    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello World,I am consumer!";
    }

    @GetMapping("/send")
    public String send(@RequestParam("msg") String msg){
        amqpTemplate.convertAndSend("hello",msg);
        return msg;
    }

    @GetMapping("/getId")
    public String getId(){
        return String.valueOf(snowFlake.nextId());
    }



}
