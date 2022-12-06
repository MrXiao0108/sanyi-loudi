package com.dzics.sanymom;

import com.dzics.sanymom.server.netty.TCPServer;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ZhangChengJun
 * Date 2021/5/24.
 * @since
 */
@SpringBootApplication(scanBasePackages = {"com.dzics.sanymom", "com.dzics.common"})
@MapperScan(basePackages = "com.dzics.common.dao")
@EnableTransactionManagement
@EnableScheduling
@EnableRabbit
@EnableCaching
@RequiredArgsConstructor
public class SanymomApplication {
    public static void main(String[] args) {
        SpringApplication.run(SanymomApplication.class, args);
    }



    private final TCPServer tcpServer;
    /**
     * This can not be implemented with lambda, because of the spring framework limitation
     * (https://github.com/spring-projects/spring-framework/issues/18681)
     *
     * @return
     */
    @SuppressWarnings({"Convert2Lambda", "java:S1604"})
    @Bean
    public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
        return new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
                tcpServer.start();
            }
        };
    }
}
