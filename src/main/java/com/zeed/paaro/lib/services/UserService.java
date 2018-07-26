package com.zeed.paaro.lib.services;


import com.zeed.generic.RestApiClient;
import com.zeed.usermanagement.models.ManagedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Value("${paaro.cliend.id:paaro-service:secret}")
    private String clientId;

    RestApiClient restApiClient = new RestApiClient();

    public Object login(ManagedUser managedUser) throws Exception {
        try {
            String base64encodedString = Base64.getEncoder().encodeToString(clientId.getBytes());
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Basic " + base64encodedString);
            MultiValueMap<String, Object> request = new LinkedMultiValueMap();
            request.add("username", managedUser.getEmail());
            request.add("password", managedUser.getPassword());
            request.add("grant_type", "password");
            String url = "http://127.0.0.1:8011/oauth/token" + "?grant_type=password&username=" + managedUser.getEmail() + "&password=" + managedUser.getPassword();
            Object object = restApiClient.apiPostAndGetClass(url,OAuth2AccessToken.class,request,headers);
            return object;
        } catch (Exception e) {
            Map<String,String> message = new HashMap<>();
            message.put("message", "unable to fetch login details");
            message.put("cause", e.getMessage());

            return message;
        }
    }

}
