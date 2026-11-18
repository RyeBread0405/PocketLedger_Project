package edu.okstate.pocketledger.account;

// thrown when someone tries to delete an account that still has transactions on it
public class AccountTransactionsException extends RuntimeException {
    
    public AccountTransactionsException(int transactionCount) {
        super("This account has " + transactionCount + " transaction(s). Deleting it will delete them too.");
    }
}
