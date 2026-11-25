package com.arcadiaeditions.fulfillment.shipping.core.application;

import com.arcadiaeditions.fulfillment.shipping.asyncapi.provider.*;
import com.arcadiaeditions.fulfillment.shipping.core.application.mappers.*;
import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.*;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.dtos.*;
import com.arcadiaeditions.fulfillment.shipping.core.outbound.jpa.*;
// import com.arcadiaeditions.fulfillment.shipping.events.avro.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing [Fulfillment].
 */
@Service
@Transactional
@org.jspecify.annotations.NullMarked
public class FulfillmentShippingServiceImpl implements FulfillmentShippingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final FulfillmentShippingServiceMapper fulfillmentShippingServiceMapper =
            FulfillmentShippingServiceMapper.INSTANCE;

    private final FulfillmentRepository fulfillmentRepository;

    private final EventsMapper eventsMapper = EventsMapper.INSTANCE;
    private final FulfillmentShippingEventsProducer eventsProducer;

    /**
     * Constructor.
     */
    public FulfillmentShippingServiceImpl(
            FulfillmentRepository fulfillmentRepository, FulfillmentShippingEventsProducer eventsProducer) {
        this.fulfillmentRepository = fulfillmentRepository;
        this.eventsProducer = eventsProducer;
    }

    public Fulfillment scheduleFulfillment(ScheduleFulfillmentInput input) {

        log.debug("Request scheduleFulfillment: {}", input);

        var fulfillment = fulfillmentShippingServiceMapper.update(new Fulfillment(), input);
        // TODO: implement this method
        // emit events
        var fulfillmentScheduled = eventsMapper.asFulfillmentScheduled(fulfillment);
        eventsProducer.onFulfillmentScheduled(fulfillmentScheduled);
        var fulfillmentFailed = eventsMapper.asFulfillmentFailed(fulfillment);
        eventsProducer.onFulfillmentFailed(fulfillmentFailed);
        return fulfillment;
    }

    public Optional<Fulfillment> recordPaymentCaptured(RecordPaymentCapturedInput input) {

        log.debug("Request recordPaymentCaptured: {}", input);

        var fulfillment = fulfillmentShippingServiceMapper.update(new Fulfillment(), input);
        // TODO: implement this method
        return Optional.ofNullable(fulfillment);
    }

    public Fulfillment markReadyToShip(Long id) {

        log.debug("Request markReadyToShip: {}", id);

        var fulfillment = fulfillmentRepository
                .findById(id)
                .map(existingFulfillment -> {
                    FulfillmentTransitions.ensureCanMarkReadyToShip(existingFulfillment);
                    existingFulfillment.setStatus(FulfillmentStatus.READY_TO_SHIP);
                    return existingFulfillment;
                })
                .map(fulfillmentRepository::save)
                .orElseThrow();
        // emit events
        var fulfillmentReadyToShip = eventsMapper.asFulfillmentReadyToShip(fulfillment);
        eventsProducer.onFulfillmentReadyToShip(fulfillmentReadyToShip);
        return fulfillment;
    }

    public Optional<Fulfillment> cancelFulfillment(CancelFulfillmentInput input) {

        log.debug("Request cancelFulfillment: {}", input);

        // TODO CUSTOM_REQUIRED: load the Fulfillment this transition applies to, e.g. by a correlation key in the input
        Optional<Fulfillment> foundFulfillment = Optional.empty();
        var fulfillment = foundFulfillment
                .map(existingFulfillment -> {
                    FulfillmentTransitions.ensureCanCancelFulfillment(existingFulfillment);
                    fulfillmentShippingServiceMapper.update(existingFulfillment, input);
                    existingFulfillment.setStatus(FulfillmentStatus.CANCELLED);
                    return existingFulfillment;
                })
                .map(fulfillmentRepository::save);
        if (fulfillment.isPresent()) {
            // emit events
            var fulfillmentCancelled = eventsMapper.asFulfillmentCancelled(fulfillment.get());
            eventsProducer.onFulfillmentCancelled(fulfillmentCancelled);
        }
        return fulfillment;
    }
}
