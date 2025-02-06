package com.example.notes.exceptions;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message){
        super(message);
    }
}
