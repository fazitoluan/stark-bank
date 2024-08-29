package com.webhookcallbackreceiver.enumeration;

public enum LogStatusEnum {
    CREATED("created"),
    PAID("paid"),
    OVERDUE("overdue"),
    REVERSED("reversed"),
    VOIDED("voided"),
    EXPIRED("expired");

    private final String value;

    LogStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
