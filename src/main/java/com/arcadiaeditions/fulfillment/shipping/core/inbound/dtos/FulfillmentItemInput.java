package com.arcadiaeditions.fulfillment.shipping.core.inbound.dtos;

import com.arcadiaeditions.fulfillment.shipping.core.domain.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.*;
import java.time.*;
import java.util.*;

/**
 * FulfillmentItemInput.
 */
public class FulfillmentItemInput implements Serializable {

    @NotNull
    private String sku;

    @NotNull
    private Integer quantity;

    public String getSku() {
        return sku;
    }

    public FulfillmentItemInput setSku(String sku) {
        this.sku = sku;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public FulfillmentItemInput setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
