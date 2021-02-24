package com.group13.tcsprojectgrading.services.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.user.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersSyncService {
    private final CanvasApi canvasApi;
    private final AccountService accountService;

    @Autowired
    public UsersSyncService(AccountService accountService, CanvasApi canvasApi) {
        this.accountService = accountService;
        this.canvasApi = canvasApi;
    }

    public void syncUser() throws JsonProcessingException {
        String user = canvasApi.getCanvasUsersApi().getAccount();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(user);

        accountService.addNewAccount(new Account(
                json.get("id").asText(),
                json.get("name").asText(),
                json.get("login_id").asText(),
                json.get("short_name").asText(),
                json.get("sortable_name").asText(),
                json.get("primary_email").asText()
        ));
    }
}
