package com.qinghuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TourismServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourismServerApplication.class, args);
    }

}
