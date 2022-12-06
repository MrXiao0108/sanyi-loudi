package com.dzics.sanymom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * http情况模板配置类
 *
 * @author ZhangChengJun
 * Date 2021/6/16.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(60 * 1000);
        factory.setConnectTimeout(60 * 1000);
        factory.setReadTimeout(60 * 1000);
        final RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

}
