package com.shoppy.utils;

import com.shoppy.exception.APIException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import java.util.stream.Collectors;

public final class ControllerUtils {

    public static void checkForErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new APIException(getErrorsAsString(errors), HttpStatus.BAD_REQUEST);
        }
    }

    public static String getErrorsAsString(Errors errors) {
        return errors.getAllErrors().stream().map(error -> String.valueOf(error.getDefaultMessage())).collect(Collectors.joining(", "));
    }

    private ControllerUtils() {
        // ...
    }

}
