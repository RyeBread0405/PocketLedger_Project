package edu.okstate.pocketledger.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    BigDecimal amount,
    String label,
    String description,
    LocalDate date,
    Integer categoryId,
    Long accountId
){

    public static TransactionResponse from(Transaction transaction){
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getLabel(),
            transaction.getDescription(),
            transaction.getDate(),
            transaction.getCategory().getId(),
            transaction.getAccount().getId()
        );
    }
}