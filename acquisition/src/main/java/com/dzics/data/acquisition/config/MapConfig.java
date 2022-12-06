package com.dzics.data.acquisition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "orderline")
public class MapConfig {


    /**
     * 订单对应的IP
     */
    private Map<String, String> maps;



}
