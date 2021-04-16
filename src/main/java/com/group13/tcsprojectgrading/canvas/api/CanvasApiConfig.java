package com.group13.tcsprojectgrading.canvas.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * configurations for canvas api
 */
@Configuration
public class CanvasApiConfig {
    @Autowired
    private WebClient webClient;

    @Bean
    public CanvasApi canvasApi(WebClient webClient) {
        return new CanvasApi(webClient);
    }
}
