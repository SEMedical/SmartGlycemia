package edu.tongji.backend.exception;

public class AuthorityException extends RuntimeException{
    public AuthorityException() {
        super();
    }

    public AuthorityException(String message) {
        super("Expected authority exception under glycemia module:"+message);
    }

}
