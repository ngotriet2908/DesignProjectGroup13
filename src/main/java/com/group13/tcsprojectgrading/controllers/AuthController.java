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
    /*
    Protected method that forces the server to check the session cookie.
     */
    @RequestMapping(value = "api/auth/session", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected String verifySession() {
        return "";
    }
}
