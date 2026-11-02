package edu.okstate.pocketledger.budget;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// per category amounts that make up one budget
public interface BudgetEntryRepository extends JpaRepository<BudgetEntry, Long> {
    
    // every category line belonging to one budget
    List<BudgetEntry> findByBudget_Id(Long budgetId);
}
