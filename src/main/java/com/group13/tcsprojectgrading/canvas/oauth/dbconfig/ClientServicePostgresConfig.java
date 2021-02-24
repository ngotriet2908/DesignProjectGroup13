package com.group13.tcsprojectgrading.canvas.oauth.dbconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.List;
import java.util.function.Function;

@Configuration
public class ClientServicePostgresConfig {
    @Bean
    public OAuth2AuthorizedClientService oauth2AuthorizedClientService(
            JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
        // register new jdbc service
        JdbcOAuth2AuthorizedClientService authorizedClientService = new JdbcOAuth2AuthorizedClientService(
                jdbcOperations, clientRegistrationRepository);
        authorizedClientService.setAuthorizedClientParametersMapper(oauth2AuthorizedClientParametersMapper());
        return authorizedClientService;
    }

    // create a new mapper for jdbc
    public Function<JdbcOAuth2AuthorizedClientService.OAuth2AuthorizedClientHolder, List<SqlParameterValue>> oauth2AuthorizedClientParametersMapper() {
        return new PostgreSqlOAuth2AuthorizedClientParametersMapper();
    }
}