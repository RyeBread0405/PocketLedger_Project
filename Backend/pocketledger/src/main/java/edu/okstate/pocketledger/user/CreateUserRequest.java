package edu.okstate.pocketledger.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

//The JSON sent from frontend to create a user and the rules
public record CreateUserRequest(
    @NotBlank @Email String email, //Required
    @NotBlank @Size(min = 8, max = 72) //Required. Must be between 8-72 characters
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[^\\r\\n]*$",
            message = "Password must contain at least one letter and one number")
    String password
    )
{}


