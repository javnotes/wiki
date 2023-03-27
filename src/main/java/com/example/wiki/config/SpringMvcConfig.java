package com.example.wiki.config;

import com.example.wiki.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author luf
 * @description: 拦截器配置类，配置拦截器拦截、不拦截的路径等，
 * 拦截器的作用是在请求到达controller之前，对请求进行拦截，进行一些操作，比如登录拦截、权限拦截等
 * 优势：可以针对URL进行拦截，而不是针对方法进行拦截
 * @date 2023/03/07 20:47
 **/
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                //拦截所有请求
                .addPathPatterns("/**")
                //不拦截的请求
                .excludePathPatterns(
                        "/test/**",
                        "/redis/**",
                        "/user/login",
                        "/category/all",
                        "/ebook/list",
                        "/doc/all/**",
                        "/doc/vote/**",
                        "/doc/find-content/**",
                        "/ebook-snapshot/**"
                );
    }
}
