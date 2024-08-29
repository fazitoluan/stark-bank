package com.invoiceissuer.enumeration;

public enum InvoiceBodyParamEnum {
    AMOUNT("amount"),
    DUE("due"),
    TAX_ID("taxId"),
    NAME("name"),
    EXPIRATION("expiration"),
    DISCOUNTS("discounts"),
    DESCRIPTIONS("descriptions"),
    RULES("rules"),
    FINE("fine"),
    INTEREST("interest"),
    TAGS("tags"),
    KEY("key"),
    VALUE("value"),
    PERCENTAGE("percentage");

    private final String value;

    InvoiceBodyParamEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
