package edu.okstate.pocketledger.budget;

import java.math.BigDecimal;

import edu.okstate.pocketledger.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


// how much of a budget is allotted to one category
@Entity 
@Table(name = "budget_entry")
public class BudgetEntry {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private Category category;

    @Column(name = "allotted", nullable = false, precision = 14, scale = 2)
    private BigDecimal allotted;

    protected BudgetEntry() {}

    public BudgetEntry(Budget budget, Category category, BigDecimal allotted) {
        this.budget = budget;
        this.category = category;
        this.allotted = allotted;
    }

    public Long getId() {
        return id;
    }

    public Budget getBudget() {
        return budget;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getAllotted() {
        return allotted;
    }
}
