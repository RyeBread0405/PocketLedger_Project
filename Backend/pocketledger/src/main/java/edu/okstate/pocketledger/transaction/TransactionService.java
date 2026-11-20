package edu.okstate.pocketledger.transaction;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.okstate.pocketledger.account.Account;
import edu.okstate.pocketledger.account.AccountRepository;
import edu.okstate.pocketledger.category.Category;
import edu.okstate.pocketledger.category.CategoryRepository;
import edu.okstate.pocketledger.common.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.security.access.AccessDeniedException;

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

        //Define the catgory owner Id
        Integer categoryOwnerId = category.getUser().getId();

        if (!categoryOwnerId.equals(userId) && !categoryOwnerId.equals(1)) {
            throw new AccessDeniedException("User does not have access to this category");
        }

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

    // Delete transaction if belonging to the logged in user
    public void deleteTransaction(Long transactionId, Integer userId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new NotFoundException("Transaction not found"));

        // a transaction is yours if its account is rule
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("User does not have access to this transaction");
        }

        // Hard delete
        transactionRepository.delete(transaction);
    }

    // update existing transaction
    @Transactional 
    public Transaction updateTransaction(Long transactionId, UpdateTransactionRequest request, Integer userId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new NotFoundException("Transaction not found"));

        // check against account to see if you can edit
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("User does not have access to this transaction");
        }

        // same category rule as creating 
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new NotFoundException("Category not found"));

        Integer categoryOwnerId = category.getUser().getId();

        if (!categoryOwnerId.equals(userId) && !categoryOwnerId.equals(1)) {
            throw new AccessDeniedException("User does not have access to this category");
        }

        transaction.update(request.amount(), request.label(), request.description(), request.date(), category);
        return transactionRepository.save(transaction);
    }
}