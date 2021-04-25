package com.smartbear.exception;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorResponseHandler {
    @ExceptionHandler(value = {InvalidPageException.class, InvalidQueryException.class})
    public ResponseEntity<Object> handleException(Exception e, WebRequest webRequest) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
