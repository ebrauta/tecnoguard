package com.github.tecnoguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class TecnoguardApplication {
    public static void main(String[] args) {
        SpringApplication.run(TecnoguardApplication.class, args);
    }
}
