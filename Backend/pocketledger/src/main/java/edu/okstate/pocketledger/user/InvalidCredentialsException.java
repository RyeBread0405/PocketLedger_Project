package edu.okstate.pocketledger.user;


// thrown wehn email OR password is wrong
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email or password is incorrect.");
    }
}
