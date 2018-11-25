package com.lukdut.monitoring.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@IntegrationComponentScan
@EnableCaching
@EnableSwagger2
public class MonitoringBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitoringBackendApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lukdut.monitoring.backend.rest"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    private SecurityScheme securityScheme() {
        return new BasicAuth("spring_oauth");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        Collections.singletonList(new SecurityReference("spring_oauth", new AuthorizationScope[]{})))
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }
}
