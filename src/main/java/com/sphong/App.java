package com.sphong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication
@EnableSpringConfigured
@EnableLoadTimeWeaving
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
