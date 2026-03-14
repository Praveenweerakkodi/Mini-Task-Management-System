package com.taskmanager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication combines three annotations:

@SpringBootApplication
public class TaskManagementApplication {

    // This is the main method - Java starts execution from here
    public static void main(String[] args) {

        SpringApplication.run(TaskManagementApplication.class, args);
    }
}
