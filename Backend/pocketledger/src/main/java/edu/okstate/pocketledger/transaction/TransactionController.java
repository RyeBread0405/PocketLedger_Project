package edu.okstate.pocketledger.transaction;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Create a new transaction
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(
            @Valid @RequestBody CreateTransactionRequest request,
            JwtAuthenticationToken authentication) {

        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        Transaction transaction = transactionService.createTransaction(request, userId);

        return TransactionResponse.from(transaction);
    }

    // Retrieve transactions for a specific account
    @GetMapping
    public List<TransactionResponse> getByAccount(
            @RequestParam Long accountId,
            JwtAuthenticationToken authentication) {

        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        return transactionService.getTransactionsByAccount(accountId, userId)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }
}