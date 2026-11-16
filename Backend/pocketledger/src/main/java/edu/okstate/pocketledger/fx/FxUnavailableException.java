package edu.okstate.pocketledger.fx;

// rate provider is down or sent something we couldn't read
public class FxUnavailableException extends RuntimeException {
    
    public FxUnavailableException() {
        super("Exchange rates are currently unavailable. Try again later.");
    }
}
