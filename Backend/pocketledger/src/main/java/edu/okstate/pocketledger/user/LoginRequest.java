package edu.okstate.pocketledger.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


// what frontend sends to log in
public record LoginRequest (
    @NotBlank @Email String email,
    @NotBlank String password
) {}
