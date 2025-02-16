package com.ilo.energyallocation.common.exception;

import org.springframework.http.HttpStatus;

public class TokenException extends CustomException {
    public TokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}