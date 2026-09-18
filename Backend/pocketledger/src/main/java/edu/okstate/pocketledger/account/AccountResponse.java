package edu.okstate.pocketledger.account;

public record AccountResponse(String name, AccountType type, String currency) {}