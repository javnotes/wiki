//package com.example.wiki.config;
//
//import com.example.wiki.interceptor.LogInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.annotation.Resource;
//
///**
// * @author luf
// * @date 2023/03/07 20:47
// **/
//@Configuration
//public class SpringMcvConfig implements WebMvcConfigurer {
//    @Resource
//    private LogInterceptor logInterceptor;
//
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(logInterceptor)
//                .addPathPatterns("/**") // 针对所有请求
//                .excludePathPatterns("/login"); // 不拦截登录请求
//
//    }
//}
