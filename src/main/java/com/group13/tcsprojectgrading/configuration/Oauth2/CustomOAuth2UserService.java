package com.group13.tcsprojectgrading.configuration.Oauth2;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static com.group13.tcsprojectgrading.model.user.ApplicationUserRole.TEACHING_ASSISTANT;

public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String tmp = userRequest.getAdditionalParameters().get("user").toString();
        Map<String, String> userMap = CustomOauth2User.oauth2UserParsing(tmp);

        OAuth2User oAuth2User = new CustomOauth2User(TEACHING_ASSISTANT.getGrantedAuthorities(),
                userRequest.getAdditionalParameters(),
                "user",
                userRequest.getAccessToken(),
                userMap.get("id"),
                userMap.get("global_id"),
                userMap.get("name"));


        System.out.println(oAuth2User);
        return oAuth2User;
    }

}
