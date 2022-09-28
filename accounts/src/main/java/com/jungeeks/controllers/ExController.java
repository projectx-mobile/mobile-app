package com.jungeeks.controllers;

import com.jungeeks.exceptionhandler.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExController extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<Object> handleMethodRuntimeException(RuntimeException runtimeException) {
        log.warn(String.format("Bad request by %s", runtimeException.getMessage()));
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleMethodUserNotFoundException(UserNotFoundException userNotFoundException) {
        log.warn(String.format("Bad request by %s", userNotFoundException.getMessage()));
        return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
