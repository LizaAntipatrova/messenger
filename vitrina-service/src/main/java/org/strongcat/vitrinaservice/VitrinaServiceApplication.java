package org.strongcat.vitrinaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication (scanBasePackages = {
        "org.strongcat.vitrinaservice",
        "com.yazikochesalna.common"
})
@EnableScheduling
public class VitrinaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VitrinaServiceApplication.class, args);
    }

}
