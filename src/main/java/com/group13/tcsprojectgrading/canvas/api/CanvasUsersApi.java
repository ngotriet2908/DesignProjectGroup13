package com.group13.tcsprojectgrading.canvas.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * This class consists of methods that are used to retrieve data from canvas user apis
 */
@Component
public class CanvasUsersApi {
    private final CanvasApi canvasApi;

    @Autowired
    public CanvasUsersApi(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

    /**
     * get authorised client account from canvas
     * @return canvas account json string
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
     * get authorised client account from canvas
     * @param userId canvas user id
     * @return canvas account json string
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
     * get user enrollment in Canvas
     * @param userId canvas user id
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
     * send canvas message to user/group
     * @param userId canvas user id
     * @param groupId canvas group id
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

