package com.dzics.business.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

/**
 * 初始化注入的bean
 *
 * @author ZhangChengJun
 * Date 2021/1/15.
 * @since
 */
@Configuration
public class InitBean {
    @Bean
    public AntPathMatcher antPathMatcher(){
        return new AntPathMatcher();
    }
}
