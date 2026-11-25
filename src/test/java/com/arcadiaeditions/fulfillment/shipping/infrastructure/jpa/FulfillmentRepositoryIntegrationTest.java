package com.arcadiaeditions.fulfillment.shipping.infrastructure.jpa;

import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import com.arcadiaeditions.fulfillment.shipping.core.outbound.jpa.FulfillmentRepository;
import jakarta.persistence.EntityManager;
import java.time.*;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FulfillmentRepositoryIntegrationTest extends BaseRepositoryIntegrationTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    FulfillmentRepository fulfillmentRepository;

    @Test
    void findAllTest() {
        var results = fulfillmentRepository.findAll();
        Assertions.assertFalse(results.isEmpty());
    }

    @Test
    void findByIdTest() {
        var id = 1L;
        var fulfillment = fulfillmentRepository.findById(id).orElseThrow();
        Assertions.assertNotNull(fulfillment.getId());
        Assertions.assertNotNull(fulfillment.getVersion());
    }

    @Test
    void saveTest() {
        Fulfillment fulfillment = new Fulfillment();
        fulfillment.setFulfillmentId("");
        fulfillment.setOrderId("");
        fulfillment.setCustomerId("");
        fulfillment.setStatus(FulfillmentStatus.values()[0]);
        fulfillment.setShippingAddress(new ShippingAddress());
        fulfillment.setItems(List.of(new FulfillmentItem()));
        fulfillment.setCarrierCode("");
        fulfillment.setTrackingNumber("");
        fulfillment.setScheduledAt(Instant.now());
        fulfillment.setReadyAt(Instant.now());
        fulfillment.setFailedAt(Instant.now());
        fulfillment.setFailureReason("");
        fulfillment.setPaymentCapturedAt(Instant.now());
        fulfillment.setCancelledAt(Instant.now());
        fulfillment.setCancellationReason("");

        // Persist aggregate root
        var created = fulfillmentRepository.save(fulfillment);

        // reloading to get relationships persisted by id
        entityManager.flush();
        entityManager.refresh(created);
        Assertions.assertNotNull(created.getId());
        Assertions.assertNotNull(created.getVersion());
    }

    @Test
    void updateTest() {
        var id = 1L;
        var fulfillment = fulfillmentRepository.findById(id).orElseThrow();
        fulfillment.setFulfillmentId("");
        fulfillment.setOrderId("");
        fulfillment.setCustomerId("");
        fulfillment.setStatus(FulfillmentStatus.values()[0]);
        fulfillment.setShippingAddress(new ShippingAddress());
        fulfillment.setItems(List.of(new FulfillmentItem()));
        fulfillment.setCarrierCode("");
        fulfillment.setTrackingNumber("");
        fulfillment.setScheduledAt(Instant.now());
        fulfillment.setReadyAt(Instant.now());
        fulfillment.setFailedAt(Instant.now());
        fulfillment.setFailureReason("");
        fulfillment.setPaymentCapturedAt(Instant.now());
        fulfillment.setCancelledAt(Instant.now());
        fulfillment.setCancellationReason("");

        fulfillment = fulfillmentRepository.save(fulfillment);
        Assertions.assertEquals("", fulfillment.getFulfillmentId());
        Assertions.assertEquals("", fulfillment.getOrderId());
        Assertions.assertEquals("", fulfillment.getCustomerId());
        Assertions.assertEquals(FulfillmentStatus.values()[0], fulfillment.getStatus());
        Assertions.assertEquals(new ShippingAddress(), fulfillment.getShippingAddress());
        Assertions.assertEquals(List.of(new FulfillmentItem()), fulfillment.getItems());
        Assertions.assertEquals("", fulfillment.getCarrierCode());
        Assertions.assertEquals("", fulfillment.getTrackingNumber());
        Assertions.assertEquals(Instant.now(), fulfillment.getScheduledAt());
        Assertions.assertEquals(Instant.now(), fulfillment.getReadyAt());
        Assertions.assertEquals(Instant.now(), fulfillment.getFailedAt());
        Assertions.assertEquals("", fulfillment.getFailureReason());
        Assertions.assertEquals(Instant.now(), fulfillment.getPaymentCapturedAt());
        Assertions.assertEquals(Instant.now(), fulfillment.getCancelledAt());
        Assertions.assertEquals("", fulfillment.getCancellationReason());
    }

    @Test
    void deleteTest() {
        var id = 1L;
        fulfillmentRepository.deleteById(id);
        var notFound = fulfillmentRepository.findById(id);
        Assertions.assertFalse(notFound.isPresent());
    }
}
