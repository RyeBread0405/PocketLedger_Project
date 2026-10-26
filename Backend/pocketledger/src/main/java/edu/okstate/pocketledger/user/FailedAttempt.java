package edu.okstate.pocketledger.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


// one row per failed login
// lock an account after too many in a row
@Entity 
@Table(name = "FailedAttempt")
public class FailedAttempt {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    // email that was typed
    @Column(name = "username_entry", length = 255)
    private String email;

    @Column(name = "logtime")
    private LocalDateTime logtime;

    // password entry NOT mapped
    protected FailedAttempt() {}

    public FailedAttempt(String email, LocalDateTime logtime) {
        this.email = email;
        this.logtime = logtime;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getLogTime() {
        return logtime;
    }
}
