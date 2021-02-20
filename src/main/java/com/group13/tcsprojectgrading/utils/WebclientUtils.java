package com.group13.tcsprojectgrading.utils;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

public class WebclientUtils {


    public static String getWebClientString(OAuth2AuthorizedClient authorizedClient, WebClient webClient, String resource_uri) {
        return webClient
                .get()
                .uri(resource_uri)
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
