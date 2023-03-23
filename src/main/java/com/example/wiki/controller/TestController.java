package com.example.wiki.controller;

import com.example.wiki.domain.Test;
import com.example.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author luf
 * @date 2022/10/09 10:24
 **/
@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Value("${test.Hello:test}")
    private String testHello;

    @Resource
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!" + " " + testHello;
    }

    @PostMapping("/hello")
    public String hello(String name) {
        return "Hello world," + name;
    }

    @GetMapping("/hello/list")
    public List<Test> list() {
        return testService.list();
    }


    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
        logger.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object value = redisTemplate.opsForValue().get(key);
        logger.info("key: {}, value: {}", key, value);
        return value;
    }

}
