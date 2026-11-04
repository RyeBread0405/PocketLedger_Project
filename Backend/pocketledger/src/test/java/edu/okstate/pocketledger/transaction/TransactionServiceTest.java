package edu.okstate.pocketledger.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.access.AccessDeniedException;

import edu.okstate.pocketledger.account.Account;
import edu.okstate.pocketledger.account.AccountRepository;
import edu.okstate.pocketledger.category.Category;
import edu.okstate.pocketledger.category.CategoryRepository;
import edu.okstate.pocketledger.user.User;

public class TransactionServiceTest {

    @Test
    void getAllTransactionsReturnsTransactions() {

        // Create fake repositories
        TransactionRepository transactionRepository =
            Mockito.mock(TransactionRepository.class);

        AccountRepository accountRepository =
            Mockito.mock(AccountRepository.class);

        CategoryRepository categoryRepository =
            Mockito.mock(CategoryRepository.class);

        // Create two fake transactions
        Transaction transaction1 = Mockito.mock(Transaction.class);
        Transaction transaction2 = Mockito.mock(Transaction.class);

        // Pretend the database contains these transactions
        when(transactionRepository.findAll())
            .thenReturn(List.of(transaction1, transaction2));

        // Create our service
        TransactionService service = new TransactionService(
            transactionRepository,
            accountRepository,
            categoryRepository
        );

        // Call the method we're testing
        List<Transaction> result = service.getAllTransactions();

        // Check that both transactions were returned
        assertEquals(2, result.size());
        assertEquals(transaction1, result.get(0));
        assertEquals(transaction2, result.get(1));
    }

    @Test
void getTransactionsByAccountRejectsUnauthorizedUser() {

    // Create a fake account owner
    User owner = Mockito.mock(User.class);

    // Pretend this owner's ID is 1
    when(owner.getId()).thenReturn(1);

    // Create a fake account belonging to that owner
    Account account = Mockito.mock(Account.class);
    when(account.getUser()).thenReturn(owner);

    // Create fake repositories
    TransactionRepository transactionRepository =
        Mockito.mock(TransactionRepository.class);

    AccountRepository accountRepository =
        Mockito.mock(AccountRepository.class);

    CategoryRepository categoryRepository =
        Mockito.mock(CategoryRepository.class);

    // Pretend account 10 exists
    when(accountRepository.findById(10L))
        .thenReturn(Optional.of(account));

    // Create our service
    TransactionService service = new TransactionService(
        transactionRepository,
        accountRepository,
        categoryRepository
    );

    // User 2 tries to access an account owned by user 1
    assertThrows(
        AccessDeniedException.class,
        () -> service.getTransactionsByAccount(10L, 2)
    );

    // Verify that no transactions were retrieved
    verify(transactionRepository, never())
        .findByAccount_Id(anyLong());
}

@Test
void getTransactionsByAccountAllowsOwner() {

    // Create a fake account owner
    User owner = Mockito.mock(User.class);
    when(owner.getId()).thenReturn(1);

    // Create an account belonging to User 1
    Account account = Mockito.mock(Account.class);
    when(account.getUser()).thenReturn(owner);

    // Create fake repositories
    TransactionRepository transactionRepository =
        Mockito.mock(TransactionRepository.class);

    AccountRepository accountRepository =
        Mockito.mock(AccountRepository.class);

    CategoryRepository categoryRepository =
        Mockito.mock(CategoryRepository.class);

    // Pretend Account 10 exists
    when(accountRepository.findById(10L))
        .thenReturn(Optional.of(account));

    // Pretend Account 10 has two transactions
    Transaction transaction1 = Mockito.mock(Transaction.class);
    Transaction transaction2 = Mockito.mock(Transaction.class);

    List<Transaction> expectedTransactions =
        List.of(transaction1, transaction2);

    when(transactionRepository.findByAccount_Id(10L))
        .thenReturn(expectedTransactions);

    // Create the service
    TransactionService service = new TransactionService(
        transactionRepository,
        accountRepository,
        categoryRepository
    );

    // User 1 requests transactions from their own account
    List<Transaction> result =
        service.getTransactionsByAccount(10L, 1);

    // Check that both transactions were returned
    assertEquals(expectedTransactions, result);

    // Verify that the repository was actually called
    verify(transactionRepository).findByAccount_Id(10L);
}
@Test
void createTransactionSavesTransaction() {

    // Create a fake user
    User owner = Mockito.mock(User.class);
    when(owner.getId()).thenReturn(1);

    // Create a fake account belonging to that user
    Account account = Mockito.mock(Account.class);
    when(account.getUser()).thenReturn(owner);

    // Create a fake category
    Category category = Mockito.mock(Category.class);

    // Create fake repositories
    TransactionRepository transactionRepository =
        Mockito.mock(TransactionRepository.class);

    AccountRepository accountRepository =
        Mockito.mock(AccountRepository.class);

    CategoryRepository categoryRepository =
        Mockito.mock(CategoryRepository.class);

    // Pretend the account and category exist
    when(accountRepository.findById(10L))
        .thenReturn(Optional.of(account));

    when(categoryRepository.findById(5))
        .thenReturn(Optional.of(category));

    // Create the service
    TransactionService service = new TransactionService(
        transactionRepository,
        accountRepository,
        categoryRepository
    );

    // Create a transaction request
    CreateTransactionRequest request = new CreateTransactionRequest(
    new BigDecimal("25.50"),
    "Groceries",
    "Weekly grocery shopping",
    LocalDate.of(2026, 11, 4),
    5,
    10L
);

    // Call the method
    service.createTransaction(request, 1);

    // Verify that a transaction was saved
    verify(transactionRepository).save(
        Mockito.any(Transaction.class)
    );
}
}