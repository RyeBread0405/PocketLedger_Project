package edu.okstate.pocketledger.budget;

import java.time.LocalDate;

import edu.okstate.pocketledger.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// one user's budget for one month
@Entity
@Table(name = "budget")
public class Budget {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    // stored as first day of the month
    @Column(name = "month", nullable = false)
    private LocalDate month;

    @ManyToOne 
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Budget() {}

    public Budget(LocalDate month, User user) {
        this.month = month;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getMonth() {
        return month;
    }

    public User getUser() {
        return user;
    }
}
