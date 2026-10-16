package edu.okstate.pocketledger.account;

import edu.okstate.pocketledger.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//Map row in the table to java object
@Entity
@Table(name = "account")

public class Account{

    //Primary key assigned by mySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    //User the acocunt belongs to
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //Name of the account
    @Column(name = "account_name", length = 255)
    private String name;

    //Type of the account
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType type;

    //Empty constructor
    protected Account(){

    }

    //Constructor
    public Account(User user, String name, AccountType type){
        this.user = user;
        this.name = name;
        this.type = type;
    }

    public User getUser(){
        return user;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public AccountType getType(){
        return type;
    }
}