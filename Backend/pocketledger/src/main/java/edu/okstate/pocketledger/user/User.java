package edu.okstate.pocketledger.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// map row in user table (ERD) to java object
// doesn't create table, just matches it (validate)
@Entity
@Table(name = "user")
public class User {
    
    // primary key, assigned by mysql on insert so it's never seen
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    // column is email_address in the db, email in here
    // unique stops two accounts sharing an email
    @Column(name = "email_address", nullable = false, unique = true)
    private String email;

    // credential in the db holds the hashed password
    @Column(name = "credential", nullable = false)
    private String passwordHash;

    // jpa needs an empty constructor to build the object before filling it in
    protected User() {

    }

    // what's used when registering a new user
    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
