package edu.okstate.pocketledger.account;

import org.springframework.http.HttpStatus;
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

    // Establishes the default display currency as USD
    // Accounts always initialize with the default currency (ie not sent by frontend)
    // Display currency can be adjusted via settings
    private static final String DEFAULT_CURRENCY = "USD";
    
    // 201 created with new account
    // @Valid runs the rules on CreateAccountRequest
    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        // TODO: save to MySQL and return created account with its id
        /*
         * name:                The name of the account itself, a String assigned by the user. Ex: "Bob's checking account"
         * type:                Enum for the account type (see AccountType.java). Ex: CHECKING
         * settings:            A json defining all user account settings.
         * DEFAULT_CURRENCY:    initializes the default currency as USD.
         */
         // TODO: validate settings
        return new AccountResponse(request.name(), request.type(), request.settings(), DEFAULT_CURRENCY);
    }
}

