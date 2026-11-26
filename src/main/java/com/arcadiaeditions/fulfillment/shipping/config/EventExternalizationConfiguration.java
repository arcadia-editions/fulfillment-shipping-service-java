package com.arcadiaeditions.fulfillment.shipping.config;

import io.zenwave360.modulith.events.scs.config.EnableSpringCloudStreamEventExternalization;
import org.springframework.context.annotation.Configuration;

/**
 * Transactional outbox (docs/adr/0003): the generated producers publish {@code Message<?>} events to Spring Modulith's
 * event publication registry, externalized through Spring Cloud Stream after the transaction commits.
 */
@Configuration
@EnableSpringCloudStreamEventExternalization
public class EventExternalizationConfiguration {}
