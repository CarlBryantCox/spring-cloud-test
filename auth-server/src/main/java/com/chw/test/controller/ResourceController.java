package com.chw.test.controller;

import com.chw.test.dto.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

//    @RequestMapping("/me")
//    public Principal me(Principal principal){
//        return principal;
//    }

    @RequestMapping("/me")
    public MyUserDetails me(@AuthenticationPrincipal MyUserDetails userDetails){
        return userDetails;
    }

    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }
}
