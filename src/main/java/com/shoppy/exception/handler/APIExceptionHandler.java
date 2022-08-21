package com.shoppy.exception.handler;

import com.shoppy.exception.APIException;
import com.shoppy.exception.APIResponseErrorMessage;
import com.shoppy.utils.ControllerUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<Object> handleApiException(APIException exception) {
        return ResponseEntity.status(exception.getStatus()).body(
                new APIResponseErrorMessage(exception.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new APIResponseErrorMessage(ControllerUtils.getErrorsAsString(exception.getBindingResult()))
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new APIResponseErrorMessage(cause.getMessage())
        );
    }

}
