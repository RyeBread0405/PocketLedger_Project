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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

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

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, JwtAuthenticationToken authentication) {
        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        transactionService.deleteTransaction(id, userId);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionRequest request, JwtAuthenticationToken authentication) {
        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        return TransactionResponse.from(transactionService.updateTransaction(id, request, userId));
    }
}