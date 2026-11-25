package com.arcadiaeditions.fulfillment.shipping.config;

import com.arcadiaeditions.fulfillment.shipping.asyncapi.provider.*;
import com.arcadiaeditions.fulfillment.shipping.core.application.*;
import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.*;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Services InMemory Config. It can be used standalone or with @SpringBootTest.
 */
@Configuration
@Profile("in-memory")
public class ServicesInMemoryConfig extends RepositoriesInMemoryConfig {

    protected final EventsProducerInMemoryContext eventsProducerInMemoryContext = new EventsProducerInMemoryContext();

    protected final FulfillmentShippingServiceImpl fulfillmentShippingService = new FulfillmentShippingServiceImpl(
            fulfillmentRepository(), eventsProducerInMemoryContext.fulfillmentShippingEventsProducer());

    @Bean
    public <T extends FulfillmentShippingService> T fulfillmentShippingService() {
        return (T) fulfillmentShippingService;
    }

    static List<Fulfillment> _fulfillments;

    public void reloadTestData() {

        var testDataLoader =
                new TestDataLoader(List.of(Fulfillment.class, ShippingAddress.class, FulfillmentItem.class));
        var fulfillments = _fulfillments != null
                ? _fulfillments
                : testDataLoader.loadCollectionTestDataAsObjects(Fulfillment.class);
        fulfillmentRepository().deleteAll();
        fulfillmentRepository().saveAll(fulfillments);
    }

    public EventsProducerInMemoryContext getEventsProducerInMemoryContext() {
        return eventsProducerInMemoryContext;
    }
}
