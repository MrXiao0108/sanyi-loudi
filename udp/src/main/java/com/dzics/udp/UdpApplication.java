package com.dzics.udp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动类
 *
 * @author ZhangChengJun
 * Date 2021/3/17.
 * @since
 */

@SpringBootApplication(scanBasePackages = {"com.dzics.udp"})
@EnableAsync
public class UdpApplication {
    public static void main(String[] args) {
        SpringApplication.run(UdpApplication.class, args);
    }
}
