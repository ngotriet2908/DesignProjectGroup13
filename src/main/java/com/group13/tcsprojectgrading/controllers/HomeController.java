package com.group13.tcsprojectgrading.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "redirect:/app";
    }

    @GetMapping("/app/**")
    public String app() {
        // TODO
        // check if session is there? (make /app authenticated)
        // check if token is valid?
        // replace call to session in Main.js with redirect to login
        System.out.println("Call to home.");
        return "home";
    }

//    // TODO any other request to app -> send to SPA the whole URL
//    @RequestMapping(value = "/app/**", method = RequestMethod.GET)
//    public String fallbackHandler() {
//        System.out.println("Caught pasted request.");
//        return "home";
//    }


//    // TODO any other request -> redirect to not exists 404 (spring prefers more specific paths, so this one can be used to catch all unmatched requests
//    @RequestMapping(value = "/**", method = RequestMethod.GET)
//    public String fallbackHandlerRest() {
//        System.out.println("Redirecting to home.");
//        return "redirect:/app/notfound";
//    }
}
