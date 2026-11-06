package com.example.socialapi.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
