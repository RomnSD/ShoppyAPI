package com.shoppy.enumeration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.shoppy.enumeration.converter.StringToDeliveryStatusConverter;

@JsonDeserialize(converter = StringToDeliveryStatusConverter.class)
public enum DeliveryStatus {
    ORDER_SUMMITED, PACKED, SHIPPED, DELIVERED
}
