package com.example.eCommerce.inventory_service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@Data
public class FeatureConfig {

    @Value("${features.user-tracking-enabled}")
    private boolean isUserTrackingEnabled;

    @Value("${my.variable}")
    private String myVariable;
}
