package org.strongcat.taskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication (scanBasePackages = {
        "org.strongcat.taskservice",
        "com.yazikochesalna.common"
})
@EnableConfigurationProperties

public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }

}
