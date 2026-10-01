package edu.okstate.pocketledger.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Make connection to the user table
//Essentially creating the User object
@Entity
@Table(name = "user")
public class User{
    
    //Define the user ID fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    //Define email address field
    @Column(name = "email address", nullable = false, length = 255)
    private String emailAddress;

    //Define the credential field
    @Column(name = "credential", nullable = false, length = 255)
    private String credential;

    //Allow for creating an empty user
    public User(){
    }

    //Create a user with an email and password
    public User(String emailAddress, String credential){
        this.emailAddress = emailAddress;
        this.credential = credential;
    }

    //Getters
    public Long getUserId(){
        return userId;
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public String getPassword(){
        return credential;
    }
}