package edu.okstate.pocketledger.user;


// what's sent back after registering (NO PASSWORD)
public record UserResponse(Integer id, String email) { }
