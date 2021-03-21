package com.group13.tcsprojectgrading.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.group13.tcsprojectgrading.canvas.oauth.CanvasOAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private CanvasOAuth2LoginSuccessHandler canvasOAuth2LoginSuccessHandler;

//    @Autowired
//    private GoogleAuthorizationCodeFlow flow;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(a -> a
                        .antMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin().disable()
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .logout(logout -> logout
                        .permitAll()
//                        .logoutSuccessHandler((request, response, authentication) -> {
//                            if (flow.getCredentialDataStore().containsKey(authentication.getName()))
//                                flow.getCredentialDataStore().delete(authentication.getName());
//                            response.setStatus(HttpServletResponse.SC_OK);
//                        }
//                        )
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .oauth2Login();
    }
}