package edu.okstate.pocketledger.user;


// what register AND login return (contract format)
public record AuthResponse (String token, UserResponse user) {}
