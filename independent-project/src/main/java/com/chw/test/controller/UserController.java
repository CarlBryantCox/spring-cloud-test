package com.chw.test.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
