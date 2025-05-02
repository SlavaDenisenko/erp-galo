package com.denisenko.orderservice.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${feign.retry.maxAttempts}")
    private int maxAttempts;

    @Value("${feign.retry.delay}")
    private int delay;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(delay, delay * 2L, maxAttempts);
    }
}
