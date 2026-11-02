package edu.okstate.pocketledger.budget;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// finds a user's budget for one month
// JpaRepository gives save, findById, and delete
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    // spring writes the SQL from the method name
    // returns optional because user might not have set a budget for that month yet
    Optional<Budget> findByUser_IdAndMonth(Integer userId, LocalDate month);
}
