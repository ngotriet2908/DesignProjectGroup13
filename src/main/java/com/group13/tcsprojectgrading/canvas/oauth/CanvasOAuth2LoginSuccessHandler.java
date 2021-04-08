package com.group13.tcsprojectgrading.canvas.oauth;

import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CanvasOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final UserService userService;

    public CanvasOAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handle(request, response, authentication);
//        clearAuthenticationAttributes(request);
    }

//    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        System.out.println("User signed in:");
        System.out.println(oauth2User.getAttributes());

        User user = new User(
                Long.valueOf((Integer) oauth2User.getAttributes().get("id")),
                (String) oauth2User.getAttributes().get("short_name"),
                (String) oauth2User.getAttributes().get("primary_email"),
                (String) oauth2User.getAttributes().get("login_id"),
                (String) oauth2User.getAttributes().get("avatar_url")
        );

        this.userService.saveUser(user);
        redirectStrategy.sendRedirect(request, response, "/");
    }
}
