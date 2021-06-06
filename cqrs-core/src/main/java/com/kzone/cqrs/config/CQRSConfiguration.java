package com.kzone.cqrs.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ConfigData.class)
@ComponentScan("com.kzone.cqrs")
public class CQRSConfiguration {


}
