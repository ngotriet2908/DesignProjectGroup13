//package com.group13.tcsprojectgrading.tests;
//
//import com.group13.tcsprojectgrading.config.SecurityConfig;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.context.support.WithSecurityContext;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//import static java.util.Arrays.asList;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
//
//@RunWith(SpringRunner.class)
////@WebAppConfiguration
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
////@SpringBootTest(classes = SecurityConfig.class)
////@WebMvcTest(ProjectsController.class)
//public class TestControllers {
//
//    @Autowired
//    private WebApplicationContext context;
//
////    @Autowired
////    private SecurityContext securityContext;
//
//    private MockMvc mvc;
//
//    @Before
//    public void setup() {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//    }
//
//
//    @WithMockUser(username = "160")
////    @WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
//    @Test
//    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
//        mvc.perform(
//                get("/api/users/recent")
//                        .contentType(MediaType.ALL_VALUE)
////                        .with(authentication(getOauthAuthenticationFor(createOAuth2User("156", "lalala"))))
////                        .with(oauth2Client("156"))
////                        .with(securityContext(securityContext))
//        )
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        ;
//    }
//
//    public static OAuth2User createOAuth2User(String name, String email) {
//
//        Map<String, Object> authorityAttributes = new HashMap<>();
//        authorityAttributes.put("key", "value");
//
//        GrantedAuthority authority = new OAuth2UserAuthority(authorityAttributes);
//
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("id", "156");
//        attributes.put("name", name);
//        attributes.put("email", email);
//
//        return new DefaultOAuth2User(asList(authority), attributes, "id");
//    }
//
//    public static Authentication getOauthAuthenticationFor(OAuth2User principal) {
//
//        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
//
//        String authorizedClientRegistrationId = "182170000000000115";
//
//        return new OAuth2AuthenticationToken(principal, authorities, authorizedClientRegistrationId);
//    }
//}
