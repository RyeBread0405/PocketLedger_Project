package edu.okstate.pocketledger.account;

// JSON returned after creating an account
// currency is set by server 

//Revisit: do we need a separate currency field if currency is covered by settings?
public record AccountResponse(String name, AccountType type, String settings, String currency) {} 