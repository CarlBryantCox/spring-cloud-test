package com.chw.test.controller;

import com.chw.test.dto.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ResourceController {

//    @RequestMapping("/me")
//    public Principal me(Principal principal){
//        return principal;
//    }

    @RequestMapping("/me")
    public MyUserDetails me(@AuthenticationPrincipal MyUserDetails userDetails){
        System.out.println("-----"+ LocalDateTime.now()+"------一次访问--------");
        return userDetails;
    }

    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }
}
