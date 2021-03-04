package com.chw.test.controller;

import com.chw.test.utils.zk.ZooKeeperSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/zookeeper")
@RestController
public class ZookeeperTestController {

    @GetMapping("/getLock")
    public String getLock(@RequestParam("productId") Long productId){
        ZooKeeperSession instance = ZooKeeperSession.getInstance();
        boolean flag = instance.acquireDistributedLock(productId);
        if(flag){
            return "success";
        }
        return "failure";
    }

    @GetMapping("/delLock")
    public String delLock(@RequestParam("productId") Long productId){
        ZooKeeperSession instance = ZooKeeperSession.getInstance();
        instance.releaseDistributedLock(productId);
        return "success";
    }
}
