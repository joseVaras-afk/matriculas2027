package com.Matricula2027.matricula2027;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.Matricula2027.matricula2027")
public class Matricula2027Application {

    public static void main(String[] args) {
        SpringApplication.run(Matricula2027Application.class, args);
    }
}