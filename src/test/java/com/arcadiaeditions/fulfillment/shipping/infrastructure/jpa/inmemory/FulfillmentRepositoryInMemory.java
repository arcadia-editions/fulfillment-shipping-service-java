package com.arcadiaeditions.fulfillment.shipping.infrastructure.jpa.inmemory;

import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import com.arcadiaeditions.fulfillment.shipping.core.outbound.jpa.FulfillmentRepository;
import java.math.*;
import java.time.*;
import java.util.*;

public class FulfillmentRepositoryInMemory extends InMemoryJpaRepository<Fulfillment>
        implements FulfillmentRepository {}
