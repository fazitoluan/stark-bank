package com.webhookcallbackreceiver.enumeration;

public enum TransferBodyParamEnum {
    AMOUNT("amount"),
    NAME("name"),
    TAX_ID("taxId"),
    BANK_CODE("bankCode"),
    BRANCH_CODE("branchCode"),
    ACCOUNT_NUMBER("accountNumber"),
    ACCOUNT_TYPE("accountType"),
    DESCRIPTION("description"),
    EXTERNAL_ID("externalId"),
    SCHEDULED("scheduled"),
    DISPLAY_DESCRIPTION("displayDescription"),
    TAGS("tags"),
    RULES("rules"),
    RESENDING_LIMIT("resendingLimit");

    private final String value;

    TransferBodyParamEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
