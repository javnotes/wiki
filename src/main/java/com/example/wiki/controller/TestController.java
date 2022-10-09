package com.example.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luf
 * @date 2022/10/09 10:24
 **/
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, world!";
    }
}
