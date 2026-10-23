package edu.okstate.pocketledger.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTransactionRequest(
    @NotNull BigDecimal amount, //Required, must be a valid decimal number
    @Size(max = 255) String label, //Maximum length of 255 characters
    @Size(max = 4095) String description, //Maximum length of 4095 characters
    @NotNull LocalDate date, //Required
    @NotNull Integer categoryId, //Required, must be a valid category ID
    @NotNull Long accountId //Required, must be a valid account ID
){}