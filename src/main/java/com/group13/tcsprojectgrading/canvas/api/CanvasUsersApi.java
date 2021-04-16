package com.group13.tcsprojectgrading.canvas.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Consists of methods that are used to retrieve data from Canvas user API
 */
@Component
public class CanvasUsersApi {
    private final CanvasApi canvasApi;

    @Autowired
    public CanvasUsersApi(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

    /**
     * Returns authorised client account from Canvas
     * @return Canvas account json string
     */
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

    /**
     * Returns authorised client account from Canvas
     * @param userId Canvas user id
     * @return Canvas account json string
     */
    public String getAccountWithId(Long userId) {
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

    /**
     * Returns user's enrollments in Canvas
     * @param userId Canvas user id
     * @return list of enrollments
     */
    public List<String> getEnrolments(Long userId) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {
            return null;
        } else {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.USER_ENROLMENT_PATH)
                    .build(userId);

            return this.canvasApi.sendRequestWithPagination(uri, HttpMethod.GET, authorizedClient);
        }
    }

    /**
     * Sends Canvas message to user/group
     * @param userId Canvas user id
     * @param groupId Canvas group id
     * @param subject subject of the message
     * @param body body of the message
     */
    public void sendMessageWithId(Long userId, Long groupId, String subject, String body) {
        OAuth2AuthorizedClient authorizedClient = this.canvasApi.getAuthorisedClient();

        if (authorizedClient == null) {

        } else {
            String rep = "";
            if (userId == null) {
                rep = "group_" + groupId;
            } else {
                rep = userId.toString();
            }

            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(CanvasEndpoints.SCHEME)
                    .host(CanvasEndpoints.HOST)
                    .path(CanvasEndpoints.CONVERSATIONS_PATH)
                    .queryParam("recipients[]", rep)
                    .queryParam("subject", subject)
                    .queryParam("body", body)
                    .queryParam("force_new", true)
                    .queryParam("group_conversation", (groupId != null))
                    .build(userId);

            this.canvasApi.sendRequest(uri, HttpMethod.POST, authorizedClient);
        }
    }
}

