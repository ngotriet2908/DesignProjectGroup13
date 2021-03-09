package com.group13.tcsprojectgrading.canvas.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class CanvasUsersApi {
    private final CanvasApi canvasApi;

    @Autowired
    public CanvasUsersApi(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

    public String getAccount() {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.PROFILE_URL)
                    .build().toUri();

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }

    public String getAccountWithId(String userId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.PROFILE_USER_URL)
                    .build(userId);

            return this.canvasApi.sendRequest(uri, HttpMethod.GET, authorizedClient);
        }
    }
}

