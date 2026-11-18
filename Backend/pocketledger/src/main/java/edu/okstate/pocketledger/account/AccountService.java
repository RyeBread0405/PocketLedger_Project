package edu.okstate.pocketledger.account;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.okstate.pocketledger.user.User;
import edu.okstate.pocketledger.user.UserRepository;
import edu.okstate.pocketledger.common.NotFoundException;
import edu.okstate.pocketledger.transaction.Transaction;
import edu.okstate.pocketledger.transaction.TransactionRepository;

@Service
public class AccountService{
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository, TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(CreateAccountRequest request, Integer userId){
        //Find the user associated with the account
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //Create a new account and associate it with the user
        Account account = new Account(user, request.name(), request.type());

        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts(Integer userId){
        return accountRepository.findByUser_Id(userId);
    }

    // delete an account and its transactions if the caller confirms
    // @Transactional makes sure we don't delete the transactions but not the account
    @Transactional 
    public void deleteAccount(Long accountId, Integer userId, boolean force) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("User does not have access to this account");
        }

        List<Transaction> transactions = transactionRepository.findByAccount_Id(accountId);

        // stop here so frontend can as "are you sure"
        if (!transactions.isEmpty() && !force) {
            throw new AccountTransactionsException(transactions.size());
        }

        // transactions go first since they point to account with foreign key
        transactionRepository.deleteAll(transactions);
        accountRepository.delete(account);
    }
}