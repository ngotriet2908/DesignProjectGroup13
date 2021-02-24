//package com.group13.tcsprojectgrading.canvas.oauth;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Map;
//
//public class CanvasOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        String tmp = userRequest.getAdditionalParameters().get("user").toString();
//        Map<String, String> userMap = CanvasOauth2User.oauth2UserParsing(tmp);
//
//        OAuth2User oAuth2User = new CanvasOauth2User(TEACHING_ASSISTANT.getGrantedAuthorities(),
//                userRequest.getAdditionalParameters(),
//                "user",
//                userRequest.getAccessToken(),
//                userMap.get("id"),
//                userMap.get("global_id"),
//                userMap.get("name"));
//
//        //TODO save to database
////        Account account = new Account(userMap.get("id"), userMap.get("name"));
////        accountService.addNewAccount(account);
//
//        System.out.println(oAuth2User);
//        return oAuth2User;
//    }
//
//}
