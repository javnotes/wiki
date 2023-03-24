package com.example.wiki.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许跨域访问的路径，可以任意配置，可以具体到直接请求路径，也可以是一类请求路径，如：/api/**，这里我们配置为所有请求都支持跨域，如：/api/**，这里我们配置为所有请求都支持跨域
        registry.addMapping("/**")
                // 允许跨域访问的源
                .allowedOriginPatterns("*")
                // 允许请求头，如：X-Token
                .allowedHeaders(CorsConfiguration.ALL)
                // 允许提交方法，POST、GET等
                .allowedMethods(CorsConfiguration.ALL)
                // 允许cookie跨域，如果要从cookie中获取登录信息，必须加上这句，否则前端拿不到cookie，也就无法判断用户是否登录，也就无法进行权限控制
                .allowCredentials(true)
                // 1小时内不需要再预检（发OPTIONS请求）
                .maxAge(3600);
    }

}
