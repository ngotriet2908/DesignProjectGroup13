package com.group13.tcsprojectgrading.controllers;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

/**
 * Controller handles gmail oauth2 flow
 */
@Controller
@RestController
@RequestMapping("/api/gmail")
public class GmailAuthController {

    private final GoogleAuthorizationCodeFlow flow;
    private final Map<String, String> currentUrlMap;

    @Value("${gmail.client.redirectUri}")
    private String redirectUri;

    @Autowired
    public GmailAuthController(GoogleAuthorizationCodeFlow flow) {
        this.flow = flow;
        this.currentUrlMap = new HashMap<>();
    }

    /**
     * start Gmail oauth2 flow
     * @param principal injected oauth2 client's information
     * @param request oauth2 request
     * @return redirect user to Gmail login
     * @throws Exception exception
     */
    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus1(Principal principal, HttpServletRequest request) throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
        String currentUrl = request.getHeader("Referer");
        if (currentUrl != null) {
            System.out.println("put current url " + currentUrl + " for " + principal.getName());
            this.currentUrlMap.put(principal.getName(), currentUrl);
        }
        return new RedirectView(authorizationUrl.build());
    }

    /**
     * finishing Gmail oauth2 flow
     * @param code intermediate code for Gmail oauth2 flow
     * @param principal injected oauth2 client's information
     * @return redirect user to
     * @throws IOException not found exception
     */
    @RequestMapping(value = "/code", method = RequestMethod.GET, params = "code")
    public RedirectView oauth2Callback(@RequestParam(value = "code") String code, Principal principal) throws IOException {

        if (flow.loadCredential(principal.getName()) == null) {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            Credential credential = flow.createAndStoreCredential(response, principal.getName());
            System.out.println("stored " + principal.getName() + " with token " + credential.getAccessToken());
        }
        String currentUrl = "/";
        if (this.currentUrlMap.containsKey(principal.getName())) {
            currentUrl = this.currentUrlMap.get(principal.getName());
            this.currentUrlMap.remove(principal.getName());
        }
        return new RedirectView(currentUrl);
    }
}

