package edu.okstate.pocketledger.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// the JSON sent from frontend to POST /api/accounts and the rules
public record CreateAccountRequest(
    @NotBlank @Size(max = 50) String name,  // required, 50 character max
    @NotNull AccountType type   // required, must be one of the AccountType values
 ) {}
