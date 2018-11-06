package com.lukdut.monitoring.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@IntegrationComponentScan
@EnableCaching
public class MonitoringBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitoringBackendApplication.class, args);
    }
}
