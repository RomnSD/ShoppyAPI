package com.shoppy.service;

import com.shoppy.exception.APIException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.authorization.client.util.HttpMethod;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String GRANT_TYPE = "grant_type";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_USERNAME = "username";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    @Value("${keycloak.auth-server-url}")
    private String serverURL;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak-logout-url}")
    private String logoutURL;

    @Value("${keycloak-token-url}")
    private String tokenURL;

    public AccessTokenResponse login(Principal principal, String username, String password) {
        if (principal != null) {
            throw new APIException("You are already logged in", HttpStatus.BAD_REQUEST);
        }
        return login(username, password);
    }

    public AccessTokenResponse login(String username, String password) {
        try {
            return sendRequest(tokenURL, Map.of(GRANT_TYPE, GRANT_TYPE_PASSWORD, GRANT_TYPE_USERNAME, username, GRANT_TYPE_PASSWORD, password));
        }
        catch (HttpResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                throw new APIException("Incorrect username or password", HttpStatus.UNAUTHORIZED);
            }
            else {
                log.error("Error while logging in: ", exception);
                throw new APIException("Log-in service not available", HttpStatus.SERVICE_UNAVAILABLE);
            }
        }
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        try {
            return sendRequest(tokenURL, Map.of(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN, GRANT_TYPE_REFRESH_TOKEN, refreshToken));
        }
        catch (HttpResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
                throw new APIException("Refresh token is not valid, try to log-in again", HttpStatus.BAD_REQUEST);
            }
            else {
                log.error("Error while refreshing token: ", exception);
                throw new APIException("Refresh token service not available", HttpStatus.SERVICE_UNAVAILABLE);
            }
        }
    }

    public AccessTokenResponse logout(String refreshToken) {
        try {
            return sendRequest(logoutURL, Map.of(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN, GRANT_TYPE_REFRESH_TOKEN, refreshToken));
        }
        catch (HttpResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
                throw new APIException("Refresh token is not valid", HttpStatus.BAD_REQUEST);
            }
            else {
                log.error("Error while logging out: ", exception);
                throw new APIException("Logout service not available", HttpStatus.SERVICE_UNAVAILABLE);
            }
        }
    }

    public AccessTokenResponse sendRequest(String requestURL, Map<String, String> parameters) throws HttpResponseException {
        Http http = new Http(new Configuration(serverURL, realm, clientId, null, null), (params, headers) -> {});
        HttpMethod<AccessTokenResponse> httpMethod = http.<AccessTokenResponse>post(requestURL).authentication().client().form();

        httpMethod.param("client_id", clientId);
        httpMethod.param("client_secret", clientSecret);
        parameters.forEach(httpMethod::param);

        return httpMethod.response().json(AccessTokenResponse.class).execute();
    }

}
