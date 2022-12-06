package com.dzics.kanban;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

/**
 * @author ZhangChengJun
 * Date 2021/5/10.
 * @since
 */
@SpringBootApplication(scanBasePackages = {"com.dzics.kanban"})
@MapperScan(basePackages = "com.dzics.kanban.dao")
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
public class KanbanroutingApp {
    public static void main(String[] args) {
        SpringApplication.run(KanbanroutingApp.class, args);
    }

}
