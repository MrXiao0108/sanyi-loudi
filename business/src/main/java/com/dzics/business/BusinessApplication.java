package com.dzics.business;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ZhangChengJun
 * Date 2021/1/5.
 */
@SpringBootApplication(scanBasePackages = {"com.dzics.business", "com.dzics.common"})
@MapperScan(basePackages = "com.dzics.common.dao")
@EnableTransactionManagement
@EnableScheduling
//@EnableAdminServer
@EnableCaching
public class BusinessApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessApplication.class, args);
    }

}
