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
        return "home";
    }
}
