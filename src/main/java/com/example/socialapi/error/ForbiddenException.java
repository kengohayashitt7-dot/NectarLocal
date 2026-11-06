package com.example.socialapi.error;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
