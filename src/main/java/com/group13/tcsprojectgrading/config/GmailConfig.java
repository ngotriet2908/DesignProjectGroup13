package com.group13.tcsprojectgrading.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Configuration for OAuth2 flow to get a token from Gmail for sending emails
 */
@Configuration
public class GmailConfig {

    @Value("${gmail.client.clientId}")
    private String clientId;

    @Value("${gmail.client.clientSecret}")
    private String clientSecret;

    @Value("${gmail.client.redirectUri}")
    private String redirectUri;

    private GoogleAuthorizationCodeFlow flow;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Initialises Gmail flow if not exists or returns the Gmail flow that contains users' tokens
     * @return gmail flow
     * @throws GeneralSecurityException security exception
     * @throws IOException no credentials file found
     */
    @Bean
    public GoogleAuthorizationCodeFlow getGmailFlow() throws GeneralSecurityException, IOException {
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            File file = new File("src/main/resources/gmailAuth");
            DataStoreFactory dataStore = new FileDataStoreFactory(file);
            flow = new GoogleAuthorizationCodeFlow.Builder(
                            httpTransport,
                            JSON_FACTORY,
                            clientSecrets,
                            Collections.singleton(GmailScopes.GMAIL_SEND))
                    .setAccessType("offline")
                    .setDataStoreFactory(dataStore)
                    .build();
        } else {
            System.out.println("Gmail flow exists.");
        }
        return flow;
    }

}
