package com.arcadiaeditions.fulfillment.shipping.adapters.events;

import com.arcadiaeditions.fulfillment.shipping.asyncapi.client.IOrderConfirmedEventV1ConsumerService;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.FulfillmentShippingService;
import com.arcadiaeditions.orders.checkout.events.avro.OrderConfirmed;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter: order-confirmed-event-v1 -> FulfillmentShippingService.scheduleFulfillment.
 * Stub so the Spring context boots; the generator does not emit adapters for AsyncAPIGenerator interfaces.
 */
@Component
public class OrderConfirmedConsumerService implements IOrderConfirmedEventV1ConsumerService {

    private final FulfillmentShippingService fulfillmentShippingService;

    public OrderConfirmedConsumerService(FulfillmentShippingService fulfillmentShippingService) {
        this.fulfillmentShippingService = fulfillmentShippingService;
    }

    @Override
    public void onOrderConfirmedEventV1(OrderConfirmed payload, OrderConfirmedHeaders headers) {
        // TODO CUSTOM_REQUIRED: map payload to ScheduleFulfillmentInput and call
        // fulfillmentShippingService.scheduleFulfillment
        throw new UnsupportedOperationException("Not implemented");
    }
}
