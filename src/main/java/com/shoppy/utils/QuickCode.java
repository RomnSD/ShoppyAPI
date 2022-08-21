package com.shoppy.utils;

import com.shoppy.exception.APIException;

import org.springframework.http.HttpStatus;

public final class QuickCode {

    public static<T> T getNotNull(T object, String message, HttpStatus status) {
        if (object == null) {
            throw new APIException(message, status);
        }
        return object;
    }

    public static void assertTrue(boolean condition, String message, HttpStatus status) {
        if (!condition) {
            throw new APIException(message, status);
        }
    }

    private QuickCode() {
        // ...
    }

}
