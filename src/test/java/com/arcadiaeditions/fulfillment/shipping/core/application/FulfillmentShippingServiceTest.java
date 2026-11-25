package com.arcadiaeditions.fulfillment.shipping.core.application;

import static org.mockito.Mockito.*;

import com.arcadiaeditions.fulfillment.shipping.config.*;
import com.arcadiaeditions.fulfillment.shipping.core.application.mappers.*;
import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.*;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.dtos.*;
import com.arcadiaeditions.fulfillment.shipping.core.outbound.jpa.*;
import com.arcadiaeditions.fulfillment.shipping.infrastructure.jpa.inmemory.*;
import java.time.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Acceptance Test for FulfillmentShippingService.
 */
class FulfillmentShippingServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    ServicesInMemoryConfig context = new ServicesInMemoryConfig();
    FulfillmentShippingServiceImpl fulfillmentShippingService = context.fulfillmentShippingService();

    FulfillmentRepositoryInMemory fulfillmentRepository = context.fulfillmentRepository();

    @BeforeEach
    void setUp() {
        context.reloadTestData();
    }

    @Test
    void scheduleFulfillmentTest() { // TODO: implement this test
    }

    @Test
    void recordPaymentCapturedTest() { // TODO: implement this test
    }

    @Test
    void markReadyToShipTest() { // TODO: implement this test
    }

    @Test
    void cancelFulfillmentTest() { // TODO: implement this test
    }
}
