package com.group13.tcsprojectgrading.canvas.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


public class CanvasApi {
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    private final WebClient webClient;

    private final CanvasUsersApi canvasUsersApi;
    private final CanvasCoursesApi canvasCoursesApi;

    /**
     * Creates a new Canvas API accessor.
     * @param webClient spring boot webclient
     */
    public CanvasApi(WebClient webClient) {
        this.webClient = webClient;
        this.canvasUsersApi = new CanvasUsersApi(this);
        this.canvasCoursesApi = new CanvasCoursesApi(this);
    }

    /**
     * Returns Canvas users API object, which grants the ability to call Canvas users' endpoints
     * @return Canvas users api
     */
    public CanvasUsersApi getCanvasUsersApi() {
        return canvasUsersApi;
    }

    /**
     * Returns Canvas courses api, which grants the ability to call Canvas courses' endpoints
     * @return Canvas courses api
     */
    public CanvasCoursesApi getCanvasCoursesApi() {
        return canvasCoursesApi;
    }

    /**
     * Returns user OAuth2 authenticated client from user request's parameters and database
     * @return oauth2 authorised client
     */
    public OAuth2AuthorizedClient getAuthorisedClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        OAuth2AuthorizedClient authorizedClient =
                this.authorizedClientService.loadAuthorizedClient("canvas", authentication.getName());

        if(authorizedClient != null && authorizedClient.getAccessToken() != null) {
            return authorizedClient;
        } else {
            return null;
        }
    }

    /**
     * Sends CRUD request to Canvas' endpoints.
     * @param uri Canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains Canvas token
     * @return response body from Canvas
     */
    public String sendRequest(URI uri, HttpMethod httpMethod, OAuth2AuthorizedClient authorizedClient) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(httpMethod);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uri);
        bodySpec
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient));
        return bodySpec
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Sends POST request to Canvas' endpoints
     * @param uri Canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains Canvas token
     * @param data payload for post request
     * @return response body from Canvas
     */
    public String postRequest(URI uri, HttpMethod httpMethod, OAuth2AuthorizedClient authorizedClient, MultiValueMap<String, String> data) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(httpMethod);
        WebClient.RequestBodySpec bodySpec = uriSpec
                .uri(uri);
        bodySpec
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(data))
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient));
        System.out.println(uriSpec);
        return bodySpec
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Sends async CRUD request to Canvas' endpoints
     * @param uri Canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains Canvas token
     * @return a Mono entity for async methods
     */
    public Mono<ResponseEntity<String>> sendRequestAsync(URI uri, HttpMethod httpMethod, OAuth2AuthorizedClient authorizedClient) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(httpMethod);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uri);
        bodySpec.attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient));
        return bodySpec.retrieve().toEntity(String.class);
    }

    /**
     * Sends CRUD request to Canvas' endpoints and unwraps Canvas' pagination
     * @param url Canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains Canvas token
     * @return list of response bodies from Canvas
     */
    public List<String> sendRequestWithPagination(URI url, HttpMethod httpMethod, OAuth2AuthorizedClient authorizedClient) {
        Mono<List<String>> entityMono =
                sendRequestAsync(url, httpMethod, authorizedClient)
                .expand(response -> {
                    String headerLink = response.getHeaders().get("Link").get(0);
                    String[] links = headerLink.split(",");
                    String next = null;
                    for(String link: links) {
                        String actual_link = link.split("; ")[0].substring(1, link.split("; ")[0].length() - 1);
                        String header_link = link.split("; ")[1];
                        if (header_link.contains("next")) {
                            next = actual_link;
                        }
                    }
                    if (next == null) {
                        return Mono.empty();
                    }

                    URI uri = null;
                    try {
                        uri = new URI(next);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return sendRequestAsync(uri, httpMethod, authorizedClient);
                }).flatMap(clientResponse -> Mono.just(clientResponse.getBody())).collectList();

        List<String> res = entityMono.block();
        return res;
    }
}
