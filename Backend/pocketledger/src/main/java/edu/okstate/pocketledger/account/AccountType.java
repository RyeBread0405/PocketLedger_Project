package edu.okstate.pocketledger.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/*
 * AccountType.java
 * Defines an enumerated value used to define the types of accounts.
 */

// four account types from contract
public enum AccountType {
    CHECKING, SAVINGS, CREDIT, CASH;

    // translates between JSON and Java
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static AccountType fromJson(String value) {
        return AccountType.valueOf(value.toUpperCase());
    }
}
