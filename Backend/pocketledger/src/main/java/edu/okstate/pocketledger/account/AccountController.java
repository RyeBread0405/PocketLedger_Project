package edu.okstate.pocketledger.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

// endpoints for the accounts
@RestController
public class AccountController {

    // accounts always start in default currency, not sent by frontend 
    // user can change later in settings
    private static final String DEFAULT_CURRENCY = "USD";
    
    // 201 created with new account
    // @Valid runs the rules on CreateAccountRequest
    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        // TODO: save to MySQL and return created account with its id
        return new AccountResponse(request.name(), request.type(), DEFAULT_CURRENCY);
    }
}

