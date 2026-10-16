package edu.okstate.pocketledger.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import edu.okstate.pocketledger.category.Category;
import edu.okstate.pocketledger.account.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
 * Transaction.java
 * Maps rows in the table Transaction to a Java object.
 * Validates, does not create.
 */

// validation
@Entity
@Table(name = "transaction")
public class Transaction{

    //Primary key assigned by mysql
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    //Transaction amount (delta on the table)
    @Column(name = "delta", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    //Transaction label | Max length 255
    @Column(name = "transaction_label", length = 255)
    private String label;
 
    //Transaction description | Max length 4095
    @Column(name = "transaction_description", length = 4095)
    private String description;

    //Transaction Date
    @Column(name = "transaction_date", nullable = false) //Required
    private LocalDate date;

    //Category
    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private Category category;

    //Account
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    //Empty constructor for JPA
    protected Transaction(){
    }

    //Constructor with parameters
    public Transaction(BigDecimal amount, String label, String description, LocalDate date, Category category, Account account){
        this.amount = amount;
        this.label = label;
        this.description = description;
        this.date = date;
        this.category = category;
        this.account = account;
    }

    // accessor methods
    public Long getId(){
        return id;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public String getLabel(){
        return label;
    }

    public String getDescription(){
        return description;
    }

    public LocalDate getDate(){
        return date;
    }

    public Category getCategory(){
        return category;
    }

    public Account getAccount(){
        return account;
    }
}