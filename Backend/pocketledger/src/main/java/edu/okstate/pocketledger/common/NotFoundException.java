package edu.okstate.pocketledger.common;

// thrown when something was asked for in the request but not found
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
