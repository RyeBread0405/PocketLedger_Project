package edu.okstate.pocketledger.transaction;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService{
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    //Save a new transaction
    public Transaction saveTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    //Retrieve all transactions
    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    //Retrieve a specific transaction via ID
    public List<Transaction> getTransactionsByAccount(Long accountId){
        return transactionRepository.findByAccount_Id(accountId);
    }
}