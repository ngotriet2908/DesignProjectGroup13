package com.group13.tcsprojectgrading.controller;

import com.group13.tcsprojectgrading.utils.WebclientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.group13.tcsprojectgrading.configuration.Oauth2.CustomOauth2User;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("api/basic")
public class BasicApiController {

    @Value("${spring.security.oauth2.client.provider.canvas.user-info-uri}")
    private String resource_uri;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private WebClient webClient;

    @GetMapping("/secured")
    public String getSecuredContent(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String body = WebclientUtils.getWebClientString(authorizedClient, webClient, resource_uri);
        System.out.println(body);

        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();

        return authorizedClient.getAccessToken().getTokenValue();
    }

    @GetMapping("/username")
    public String getUsername(OAuth2AuthenticationToken authentication, HttpServletRequest request) {

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String body = WebclientUtils.getWebClientString(authorizedClient, webClient, resource_uri);
//        System.out.println(body);

        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();

        return oauth2User.getUserId() + " - " + oauth2User.getName();
    }

    @GetMapping("/hello")
    public String getHello() {
        return "Hello stuffs";
    }
}
