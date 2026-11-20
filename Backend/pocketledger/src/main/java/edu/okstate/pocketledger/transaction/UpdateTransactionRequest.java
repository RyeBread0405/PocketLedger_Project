package edu.okstate.pocketledger.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


// body of PUT /api/transactions/{id}
public record UpdateTransactionRequest (
    @NotNull BigDecimal amount,
    @NotBlank @Size(max = 255) String label,
    @Size(max = 4095) String description,
    @NotNull LocalDate date,
    @NotNull Integer categoryId
) {}
