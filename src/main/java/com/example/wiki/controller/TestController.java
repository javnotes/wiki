package com.example.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luf
 * @date 2022/10/09 10:24
 **/
@RestController
public class TestController {

    @Value("${test.Hello:test}")
    private String testHello;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!" + " " + testHello;
    }

    @PostMapping("/hello")
    public String hello(String name) {
        return "Hello world," + name;
    }
}
