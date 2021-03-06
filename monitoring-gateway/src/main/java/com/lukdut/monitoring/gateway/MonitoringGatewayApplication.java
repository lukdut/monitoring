package com.lukdut.monitoring.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@IntegrationComponentScan
@EnableScheduling
public class MonitoringGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringGatewayApplication.class, args);
    }
}
