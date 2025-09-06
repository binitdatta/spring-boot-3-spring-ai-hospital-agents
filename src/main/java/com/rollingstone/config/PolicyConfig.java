package com.rollingstone.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolicyConfig {
    @Bean
    public PolicyInterceptor policyInterceptor() { return new PolicyInterceptor(); }
}

