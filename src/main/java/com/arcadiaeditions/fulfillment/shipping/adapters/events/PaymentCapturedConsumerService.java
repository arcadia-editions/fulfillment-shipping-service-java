package com.arcadiaeditions.fulfillment.shipping.adapters.events;

import com.arcadiaeditions.fulfillment.shipping.asyncapi.client.IPaymentCapturedEventV1ConsumerService;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.FulfillmentShippingService;
import com.arcadiaeditions.payments.processing.events.avro.PaymentCaptured;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter: payment-captured-event-v1 -> FulfillmentShippingService.recordPaymentCaptured.
 * Stub so the Spring context boots; the generator does not emit adapters for AsyncAPIGenerator interfaces.
 */
@Component
public class PaymentCapturedConsumerService implements IPaymentCapturedEventV1ConsumerService {

    private final FulfillmentShippingService fulfillmentShippingService;

    public PaymentCapturedConsumerService(FulfillmentShippingService fulfillmentShippingService) {
        this.fulfillmentShippingService = fulfillmentShippingService;
    }

    @Override
    public void onPaymentCapturedEventV1(PaymentCaptured payload, PaymentCapturedHeaders headers) {
        // TODO CUSTOM_REQUIRED: map payload to RecordPaymentCapturedInput and call
        // fulfillmentShippingService.recordPaymentCaptured
        throw new UnsupportedOperationException("Not implemented");
    }
}
