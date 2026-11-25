package com.arcadiaeditions.fulfillment.shipping.core.application.mappers;

import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import com.arcadiaeditions.fulfillment.shipping.core.inbound.dtos.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {BaseMapper.class})
public interface EventsMapper {

    EventsMapper INSTANCE = Mappers.getMapper(EventsMapper.class);

    com.arcadiaeditions.fulfillment.shipping.events.avro.FulfillmentScheduled asFulfillmentScheduled(
            Fulfillment fulfillment);

    com.arcadiaeditions.fulfillment.shipping.events.avro.FulfillmentFailed asFulfillmentFailed(Fulfillment fulfillment);

    com.arcadiaeditions.fulfillment.shipping.events.avro.FulfillmentCancelled asFulfillmentCancelled(
            Fulfillment fulfillment);

    com.arcadiaeditions.fulfillment.shipping.events.avro.FulfillmentReadyToShip asFulfillmentReadyToShip(
            Fulfillment fulfillment);
}
