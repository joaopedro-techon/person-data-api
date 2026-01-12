package com.example.person.config;

import feign.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RunnerConfig implements ApplicationRunner {

    @Autowired
    private ApplicationContext context;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, Client> beans = context.getBeansOfType(feign.Client.class);
        beans.forEach((name, bean) ->
                System.out.println(name + " -> " + bean.getClass().getName())
        );
    }
}
