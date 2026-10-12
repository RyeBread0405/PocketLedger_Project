package edu.okstate.pocketledger.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

// map row in transaction table to java object
// doesn't create table, just matches it (validate)
@Entity
@Table(name = "transaction")
public class Transaction{

    //Primary key assigned by mysql
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    // Transaction amount (delta on the table)
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

    //Add category and account later. I'm lazy

}