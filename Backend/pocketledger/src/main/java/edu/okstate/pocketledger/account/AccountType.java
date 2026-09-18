package edu.okstate.pocketledger.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    CHECKING, SAVINGS, CREDIT, CASH;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static AccountType fromJson(String value) {
        return AccountType.valueOf(value.toUpperCase());
    }
}
