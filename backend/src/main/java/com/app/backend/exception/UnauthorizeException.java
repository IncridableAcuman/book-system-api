package com.app.backend.exception;

public class UnauthorizeException extends RuntimeException{
    public UnauthorizeException(String message){
        super(message);
    }
}
