package edu.okstate.pocketledger.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CurrencySettingsRequest(

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$",
             message = "Currency must be a 3-letter uppercase code (e.g., USD, EUR, JPY)")
             String currency //Set up the required format for the currency setting
){}