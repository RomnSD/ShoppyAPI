package com.shoppy.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public class APIException extends RuntimeException {

    private final HttpStatus status;

    public APIException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public APIException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
