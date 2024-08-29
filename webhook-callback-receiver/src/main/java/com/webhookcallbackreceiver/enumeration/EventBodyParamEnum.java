package com.webhookcallbackreceiver.enumeration;

public enum EventBodyParamEnum {
    CURSOR("cursor"),
    LIMIT("limit"),
    AFTER("after"),
    BEFORE("before"),
    IS_DELIVERED("isDelivered"),
    FIELDS("fields");

    private final String value;

    EventBodyParamEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
