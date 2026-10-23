package edu.okstate.pocketledger.transaction;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.okstate.pocketledger.account.Account;
import edu.okstate.pocketledger.account.AccountRepository;
import edu.okstate.pocketledger.category.Category;
import edu.okstate.pocketledger.category.CategoryRepository;

@Service
public class TransactionService{
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository
    , AccountRepository accountRepository
    , CategoryRepository categoryRepository){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    //Save a new transaction
    public Transaction saveTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    //Retrieve all transactions
    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    //Retrieve transaction belonging to a specific account
    public List<Transaction> getTransactionsByAccount(Long accountId, Integer userId){
        //Find the account
        Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        //Make sure the logged in user has access to the account
        if (!account.getUser().getId().equals(userId)){
            throw new org.springframework.security.access.AccessDeniedException("User does not have access to this account");
        }

        return transactionRepository.findByAccount_Id(accountId);
    }

    //Create a new transaction and associate it with an account and category
    public Transaction createTransaction(CreateTransactionRequest request, Integer userId){
        //Find the account associated with the transaction
        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if(!account.getUser().getId().equals(userId)){
            throw new org.springframework.security.access.AccessDeniedException("User does not have access to this account");
        }

        //Find the category associated with the transaction
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        //Create the new transaction
        Transaction transaction = new Transaction(
            request.amount(),
            request.label(),
            request.description(),
            request.date(),
            category,
            account
        );
        //Save the transaction to the database
        return transactionRepository.save(transaction);
    }
}