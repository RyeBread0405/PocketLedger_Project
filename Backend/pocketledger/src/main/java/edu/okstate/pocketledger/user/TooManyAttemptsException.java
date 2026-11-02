package edu.okstate.pocketledger.user;

public class TooManyAttemptsException extends RuntimeException {
    
    public TooManyAttemptsException() {
        super("Too many failed login attempts. Please try again in 1 hour.");
    }
}
