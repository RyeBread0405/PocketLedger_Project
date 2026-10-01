package edu.okstate.pocketledger.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//The JSON sent from frontend to create a user and the rules
public record CreateUserRequest(
    @NotBlank @Email String email, //Required
    @NotBlank @Size(min = 8, max = 72) String password //Required. Must be between 8-72 characters
) {}


