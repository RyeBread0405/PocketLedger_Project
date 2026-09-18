package edu.okstate.pocketledger.account;

// JSON returned after creating an account
// currency is set by server 
public record AccountResponse(String name, AccountType type, String currency) {}