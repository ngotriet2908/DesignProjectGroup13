package com.group13.tcsprojectgrading.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home view Controller
 */
@Controller
public class HomeController {

    /**
     * @return redirect user to /app (for consistence path)
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/app";
    }

    /**
     * @return return home.html
     */
    @GetMapping("/app/**")
    public String app() {
        return "home";
    }
}
