package com.invoiceissuer.enumeration;

public enum StatusIssueEnum {
    SUCCESSFUL(1),
    ERROR(0),
    NO_PENDING(2);

    private final int value;

    StatusIssueEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
