package com.group13.tcsprojectgrading.canvas.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Catches exceptions from Canvas request from the application
 */
@ControllerAdvice
public class CanvasExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { WebClientResponseException.class })
    protected ResponseEntity<Object> handleException(RuntimeException exception, WebRequest request) {
        System.out.println("Exception: " + exception.getMessage());
        return handleExceptionInternal(exception, null, new HttpHeaders(), ((WebClientResponseException) exception).getStatusCode(), request);
    }
}
