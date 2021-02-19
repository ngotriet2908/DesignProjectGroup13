package com.group13.tcsprojectgrading.controller;

import com.group13.tcsprojectgrading.configuration.Oauth2.CustomOauth2User;
import com.group13.tcsprojectgrading.utils.WebclientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/canvas")
public class CanvasCoursesApi {

    private final String default_uri = "https://utwente-dev.instructure.com/api/v1/users/:user_id/courses";
    private final String account_uri = "https://utwente-dev.instructure.com/api/v1/users/self/profile";
//    private final String account_uri = "https://utwente-dev.instructure.com/api/v1/accounts/self/users";

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private WebClient webClient;

    @GetMapping("/courses")
    public String getUsername(OAuth2AuthenticationToken authentication, HttpServletRequest request) {

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

//        String body = WebclientUtils.getWebClientString(authorizedClient, webClient, resource_uri);
//        System.out.println(body);
        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();


        Mono<String> response = webClient
                .get()
                .uri(default_uri.replace(":user_id", oauth2User.getUserId()))
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class);

        return response.block();
    }

    @GetMapping("/account")
    public String getAccount(OAuth2AuthenticationToken authentication, HttpServletRequest request) {

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

//        String body = WebclientUtils.getWebClientString(authorizedClient, webClient, resource_uri);
//        System.out.println(body);
        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();


        Mono<String> response = webClient
                .get()
                .uri(account_uri)
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class);

        return response.block();
    }

    @GetMapping("/hello")
    public String getHello() {
        return "Hello stuffs";
    }

}
