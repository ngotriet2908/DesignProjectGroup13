package com.group13.tcsprojectgrading.configuration.Oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomOauth2User extends DefaultOAuth2User {

    public static String ID_LABEL = "id";
    public static String GLOBAL_ID_LABEL = "global_id";
    public static String NAME_LABEL = "name";


    private OAuth2AccessToken accessToken;
    private String userId;
    private String global_id;
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey) {
        super(authorities, attributes, nameAttributeKey);
    }

    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, OAuth2AccessToken accessToken) {
        super(authorities, attributes, nameAttributeKey);
        this.accessToken = accessToken;
    }

    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, OAuth2AccessToken accessToken, String userId, String global_id) {
        super(authorities, attributes, nameAttributeKey);
        this.accessToken = accessToken;
        this.userId = userId;
        this.global_id = global_id;
    }

    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, OAuth2AccessToken accessToken, String userId, String global_id, String name) {
        super(authorities, attributes, nameAttributeKey);
        this.accessToken = accessToken;
        this.userId = userId;
        this.global_id = global_id;
        this.name = name;
    }

    public void setUsingPrincipal(String principal) {
        Map<String, String> userMap = CustomOauth2User.oauth2UserParsing(principal);
        this.userId = userMap.get("id");
        this.global_id = userMap.get("global_id");
        this.name = userMap.get("name");
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(String global_id) {
        this.global_id = global_id;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "CustomOauth2User{" +
                "accessToken=" + accessToken +
                ", userId='" + userId + '\'' +
                ", global_id='" + global_id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    public static Map<String, String> oauth2UserParsing(String principal) {
        String tmp = principal;
        tmp = tmp.substring(1,tmp.length() - 1);
        String[] tmpArr = tmp.split(", ");
        Map<String, String> map = new HashMap<>();
        for (String s: tmpArr) {
            map.put(s.split("=")[0], s.split("=")[1]);
        }
        return map;
    }
}
