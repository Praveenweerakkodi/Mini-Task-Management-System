package com.taskmanager;

// This is the main starting point of our Spring Boot application
// When you run this class, the entire web server starts up

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication combines three annotations:
// 1. @Configuration - allows defining beans
// 2. @EnableAutoConfiguration - auto-configures Spring based on dependencies
// 3. @ComponentScan - scans all classes in this package for Spring components
@SpringBootApplication
public class TaskManagementApplication {

    // This is the main method - Java starts execution from here
    public static void main(String[] args) {
        // SpringApplication.run() boots up the entire Spring application
        // It sets up the web server (Tomcat), connects to DB, and registers all controllers
        SpringApplication.run(TaskManagementApplication.class, args);
    }
}
