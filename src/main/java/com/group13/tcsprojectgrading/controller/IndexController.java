package com.group13.tcsprojectgrading.controller;

import com.group13.tcsprojectgrading.utils.WebclientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;


@Controller
public class IndexController {

    @Value("${spring.security.oauth2.client.provider.canvas.user-info-uri}")
    private String resource_uri;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private WebClient webClient;

    @Value("${spring.security.oauth2.client.provider.canvas.user-info-uri}")
    private String token_uri;

    @GetMapping("/")
    public String getIndex() {
        return "redirect:/index";
    }

    @GetMapping("/home")
    public String getHome() {
        return "redirect:/index";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/user")
    public String getUser(@RegisteredOAuth2AuthorizedClient("canvas") OAuth2AuthorizedClient authorizedClient) {

        String body = WebclientUtils.getWebClientString(authorizedClient, webClient, resource_uri);
        System.out.println(body);

        return "user";
    }
    
    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
