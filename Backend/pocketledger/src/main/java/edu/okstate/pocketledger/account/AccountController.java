package edu.okstate.pocketledger.account;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

    // Establishes the default display currency as USD
    // Accounts always initialize with the default currency (ie not sent by frontend)
    // Display currency can be adjusted via settings
    private static final String DEFAULT_CURRENCY = "USD";
    
    // 201 created with new account
    // @Valid runs the rules on CreateAccountRequest
    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request, JwtAuthenticationToken authentication) {
        

        Integer userId = Integer.valueOf(authentication.getName());

        Account account = accountService.createAccount(request, userId);

        return new AccountResponse(account.getName(), account.getType());
    }
}

