package edu.okstate.pocketledger.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * User.java
 * Maps rows in the table User to a Java object.
 * Validates, does not create.
 * TO-DO: Revisit email problem (33). See also: UserRepository.java
 * TO-DO: Someone brought up that we should check that field lengths are in bounds before we attempt to send stuff to the server (discussion
 *        2 - Thurman et al). I (Michal) concur, and while we should enforce that on the front-end as well, it might be wise to implement
 *        something back here as well. 
 */

// validation
@Entity
@Table(name = "user")
public class User {
    
    // primary key, assigned by MySQL on insert
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    // column is email_address in the db, email in here
    // unique stops two accounts sharing an email
    // TO-DO: uniqueness is not a property enforced by the DB, which seems like it could break something. Revisit this.
    // 
    @Column(name = "email_address", nullable = false, unique = true, length = 255)
    private String email;

    // credential in the db holds the hashed password
    @Column(name = "credential", nullable = false, length = 255)
    private String passwordHash;

    // jpa needs an empty constructor to build the object before filling it in
    protected User() {

    }

    // used when registering a new user
    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Accessor methods
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
