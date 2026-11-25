package com.arcadiaeditions.fulfillment.shipping.core.outbound.jpa;

import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Fulfillment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FulfillmentRepository extends JpaRepository<Fulfillment, Long> {}
