package com.example.wiki.controller;

import com.example.wiki.domain.Test;
import com.example.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luf
 * @date 2022/10/09 10:24
 **/
@RestController
public class TestController {

    @Value("${test.Hello:test}")
    private String testHello;

    @Resource
    private TestService testService;

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


}
