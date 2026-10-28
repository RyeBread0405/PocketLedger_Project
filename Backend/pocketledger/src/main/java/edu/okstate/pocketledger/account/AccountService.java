package edu.okstate.pocketledger.account;

import org.springframework.stereotype.Service;

import edu.okstate.pocketledger.user.User;
import edu.okstate.pocketledger.user.UserRepository;

@Service
public class AccountService{
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository){
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(CreateAccountRequest request, Integer userId){
        //Find the user associated with the account
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //Create a new account and associate it with the user
        Account account = new Account(user, request.name(), request.type());

        return accountRepository.save(account);
    }
}