package com.dzics.data.acquisition;

import com.dzics.data.acquisition.service.AcquiSysCmdTcpService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.dzics.data.acquisition", "com.dzics.common"})
@MapperScan(basePackages = "com.dzics.common.dao")
@EnableTransactionManagement
@EnableCaching
@EnableAsync
@EnableRabbit
public class AcquisitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcquisitionApplication.class, args);

    }

    @Autowired
    private AcquiSysCmdTcpService sysCmdTcpService;

    @PostConstruct
    public void init() {

        sysCmdTcpService.selectCmdTcpToRedis();

    }
}
