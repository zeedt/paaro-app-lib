package com.zeed.isms.lib.services;

import com.zeed.generic.RestApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class OauthService {

    private RestApiClient restApiClient = new RestApiClient();


    @Value("${oauth.authentication.url:http://127.0.0.1:8011/oauth/token}")
    public String oauthUrl;

    public ResponseEntity<OAuth2AccessToken> getACcessToken(Map<String,String> loginDetails) throws Exception {
        String base64encodedString = Base64.getEncoder().encodeToString("isms-service:secret".getBytes());
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + base64encodedString);
        MultiValueMap<String, Object> request = new LinkedMultiValueMap();
        request.add("username", loginDetails.get("username"));
        request.add("password", loginDetails.get("password"));
        request.add("grant_type", "password");
        String url = oauthUrl + "?grant_type=password&username=" + loginDetails.get("username") + "&password=" + loginDetails.get("password");
        ResponseEntity<OAuth2AccessToken> oAuth2AccessToken =
                restApiClient.apiPostAndGetResponseEntity(url, OAuth2AccessToken.class, request, headers);
        return oAuth2AccessToken;

    }


}
