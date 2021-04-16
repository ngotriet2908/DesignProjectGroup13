package com.group13.tcsprojectgrading.canvas.api;

/**
 * custom exception for canvas authorisation exception
 */
public class CanvasAuthorisationException extends Exception {
    // will replace nulls returned currently

    public CanvasAuthorisationException(String message) {
        super(message);
    }
}
