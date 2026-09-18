package edu.okstate.pocketledger.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class AccountController {

    private static final String DEFAULT_CURRENCY = "USD";
    
    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        // todo: save to MySQL and return created account with its id
        return new AccountResponse(request.name(), request.type(), DEFAULT_CURRENCY);
    }
}

