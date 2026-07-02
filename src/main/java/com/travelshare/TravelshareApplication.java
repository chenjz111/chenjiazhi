package com.travelshare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.travelshare.mapper")
@SpringBootApplication
public class TravelshareApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelshareApplication.class, args);
    }

}