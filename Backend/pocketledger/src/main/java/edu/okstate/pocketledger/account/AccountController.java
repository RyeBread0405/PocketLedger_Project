package edu.okstate.pocketledger.account;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/*
 * AccountController.java
 * Container class. Handles the meta for creating a request to create an account
 */

// endpoints for the accounts
@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    // 201 created with new account
    // @Valid runs the rules on CreateAccountRequest
    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request, JwtAuthenticationToken authentication) {
        

        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        Account account = accountService.createAccount(request, userId);

        return new AccountResponse(account.getId(),account.getName(), account.getType());
    }

   @GetMapping("/api/accounts")
    public List<AccountResponse> getAllAccounts(JwtAuthenticationToken authentication) {

        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        List<Account> accounts = accountService.getAllAccounts(userId);

        return accounts.stream()
            .map(account -> new AccountResponse(
                    account.getId(),
                    account.getName(),
                    account.getType()
            ))
            .toList();
    }
}

