package com.arcadiaeditions.fulfillment.shipping.adapters.events;

import com.arcadiaeditions.fulfillment.shipping.asyncapi.client.IOrderCancelledEventV1ConsumerService;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.FulfillmentShippingService;
import com.arcadiaeditions.orders.checkout.events.avro.OrderCancelled;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter: order-cancelled-event-v1 -> FulfillmentShippingService.cancelFulfillment.
 * Stub so the Spring context boots; the generator does not emit adapters for AsyncAPIGenerator interfaces.
 */
@Component
public class OrderCancelledConsumerService implements IOrderCancelledEventV1ConsumerService {

    private final FulfillmentShippingService fulfillmentShippingService;

    public OrderCancelledConsumerService(FulfillmentShippingService fulfillmentShippingService) {
        this.fulfillmentShippingService = fulfillmentShippingService;
    }

    @Override
    public void onOrderCancelledEventV1(OrderCancelled payload, OrderCancelledHeaders headers) {
        // TODO CUSTOM_REQUIRED: map payload to CancelFulfillmentInput and call
        // fulfillmentShippingService.cancelFulfillment
        throw new UnsupportedOperationException("Not implemented");
    }
}
