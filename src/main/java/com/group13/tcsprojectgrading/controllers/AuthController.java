package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final CanvasApi canvasApi;

    @Autowired
    public AuthController(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

    /*
    Protected method that will force the server to check the session cookie.
     */
    @RequestMapping(value = "api/auth/session", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected String verifySession() {
//        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return "";
    }
}
