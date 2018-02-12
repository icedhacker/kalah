package com.bb.kalah.exception;

public class BadRequestException extends RuntimeException {
    BadRequestException(String message) {
        super(message);
    }
}
