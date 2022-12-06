package com.dzics.queueforwarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class QueueForwardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueForwardingApplication.class, args);
    }
}
