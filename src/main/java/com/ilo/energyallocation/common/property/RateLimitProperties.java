package com.ilo.energyallocation.common.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Data
public class RateLimitProperties {
    private int capacity;
    private Duration duration;
}