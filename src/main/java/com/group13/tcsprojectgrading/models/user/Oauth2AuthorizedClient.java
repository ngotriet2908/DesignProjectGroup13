//package com.group13.tcsprojectgrading.models.user;
//
//import javax.persistence.Entity;
//
//@Entity(name = "oauth2_authorized_client")
//public class Oauth2AuthorizedClient {
//    private String clientRegistrationId;
//}


//CREATE TABLE oauth2_authorized_client (
//        client_registration_id varchar(100) NOT NULL,
//        principal_name varchar(200) NOT NULL,
//        access_token_type varchar(100) NOT NULL,
//        access_token_value bytea NOT NULL,
//        access_token_issued_at timestamp NOT NULL,
//        access_token_expires_at timestamp NOT NULL,
//        access_token_scopes varchar(1000) DEFAULT NULL,
//        refresh_token_value bytea DEFAULT NULL,
//        refresh_token_issued_at timestamp DEFAULT NULL,
//        created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
//        PRIMARY KEY (client_registration_id, principal_name)
//        );