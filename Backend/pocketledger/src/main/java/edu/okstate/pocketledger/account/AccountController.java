package edu.okstate.pocketledger.account;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    
    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccountRequest create(@Valid @RequestBody CreateAccountRequest request) {
        // todo: save to MySQL and return created account with its id
        return request;
    }
}

