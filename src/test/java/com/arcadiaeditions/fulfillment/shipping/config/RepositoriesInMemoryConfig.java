package com.arcadiaeditions.fulfillment.shipping.config;

import com.arcadiaeditions.fulfillment.shipping.core.outbound.jpa.*;
import com.arcadiaeditions.fulfillment.shipping.infrastructure.jpa.inmemory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

// @Configuration
public class RepositoriesInMemoryConfig {

    protected final FulfillmentRepository fulfillmentRepository = new FulfillmentRepositoryInMemory();

    @Bean
    @Primary
    public <T extends FulfillmentRepository> T fulfillmentRepository() {
        return (T) fulfillmentRepository;
    }
}
