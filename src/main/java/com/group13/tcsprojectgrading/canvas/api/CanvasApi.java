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
     * default constructor
     * @param webClient spring boot webclient
     */
    public CanvasApi(WebClient webClient) {
        this.webClient = webClient;
        this.canvasUsersApi = new CanvasUsersApi(this);
        this.canvasCoursesApi = new CanvasCoursesApi(this);
    }

    /**
     * get canvas users api, which grants the ability to call canvas users' endpoints
     * @return canvas users api
     */
    public CanvasUsersApi getCanvasUsersApi() {
        return canvasUsersApi;
    }

    /**
     * get canvas courses api, which grants the ability to call canvas courses' endpoints
     * @return canvas courses api
     */
    public CanvasCoursesApi getCanvasCoursesApi() {
        return canvasCoursesApi;
    }

    /**
     * get user oauth2 authentication/authorised client from user request's parameters and database
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
     * a custom method to send CRUD methods request to canvas' endpoints
     * @param uri canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains canvas token
     * @return response body from canvas
     */
    public String sendRequest(URI uri, HttpMethod httpMethod, OAuth2AuthorizedClient authorizedClient) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(httpMethod);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uri);
        bodySpec
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient));
//        return bodySpec
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
        // TODO: catch errors here and in other request functions (!important)
        String result = bodySpec
                .retrieve()
//                    .onStatus(status -> status == HttpStatus.UNAUTHORIZED,
//                            clientResponse -> Mono.error(new CanvasAuthorisationException("Unauthorised 401."))
//                    )
                .bodyToMono(String.class)
                .block();
//        System.out.println(result);
            return result;
    }

    /**
     * a custom method to send POST request to canvas' endpoints
     * @param uri canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains canvas token
     * @param data payload for post request
     * @return response body from canvas
     */
    public String postRequest(URI uri, HttpMethod httpMethod, OAuth2AuthorizedClient authorizedClient, MultiValueMap<String, String> data) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(httpMethod);
        WebClient.RequestBodySpec bodySpec = uriSpec
                .uri(uri);
        bodySpec
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(data))
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient));
//        return bodySpec
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
        // TODO: catch errors here and in other request functions (!important)
        System.out.println(uriSpec);
        String result = bodySpec
                .retrieve()
//                    .onStatus(status -> status == HttpStatus.UNAUTHORIZED,
//                            clientResponse -> Mono.error(new CanvasAuthorisationException("Unauthorised 401."))
//                    )
                .bodyToMono(String.class)
                .block();
//        System.out.println(result);
        return result;
    }

    /**
     * a custom async method to send CRUD methods request to canvas' endpoints
     * @param uri canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains canvas token
     * @return a Mono entity for async methods
     */
    public Mono<ResponseEntity<String>> sendRequestAsync(URI uri, HttpMethod httpMethod, OAuth2AuthorizedClient authorizedClient) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(httpMethod);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uri);
        bodySpec.attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient));
        return bodySpec.retrieve().toEntity(String.class);
    }

    /**
     * a custom method to send CRUD methods request to canvas' endpoints and retrieve all pages from canvas pagination
     * @param url canvas' endpoint
     * @param httpMethod CRUD method
     * @param authorizedClient oauth2 client contains canvas token
     * @return list of response bodies from canvas
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
//                        System.out.println("next == null");
                        return Mono.empty();
                    }
//                    System.out.println(next);
                    URI uri = null;
                    try {
                        uri = new URI(next);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return sendRequestAsync(uri, httpMethod, authorizedClient);
                }).flatMap(clientResponse -> Mono.just(clientResponse.getBody())).collectList();

        List<String> res = entityMono.block();
//        System.out.println(Arrays.toString(res.toArray()));
        return res;
    }
}
