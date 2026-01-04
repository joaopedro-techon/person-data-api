package com.example.person;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PersonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonApiApplication.class, args);
    }
}


