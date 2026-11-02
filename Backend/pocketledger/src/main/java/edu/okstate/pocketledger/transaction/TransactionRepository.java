package edu.okstate.pocketledger.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Long>
{
    List<Transaction> findByAccount_Id(Long accountId);

    // transactions for one user across a date range
    // for budget "spent" totals
    List<Transaction> findByAccount_User_IdAndDateBetween(Integer userId, LocalDate from, LocalDate to);
}