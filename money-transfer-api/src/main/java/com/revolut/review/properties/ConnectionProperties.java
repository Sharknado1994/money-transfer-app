package com.revolut.review.properties;

import io.micronaut.context.annotation.Value;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Getter
@Singleton
@Slf4j
public class ConnectionProperties {
    @Value("${application.h2.db-url}")
    private String dbUrl;
    @Value("${application.h2.max-pool-size}")
    private Integer maxPoolSize;
    @Value("${application.h2.idle-time}")
    private Long idleTime;
    @Value("${application.h2.driver-class-name}")
    private String driverClassName;
    @Value("${application.h2.table-name}")
    private String tableName;

    @PostConstruct
    protected void printProperties() {
        log.info("Properties db-url {} max-pool-size {} idle-time {} driver-class-name {} table-name {}", dbUrl, maxPoolSize, idleTime, driverClassName, tableName);
    }
}
