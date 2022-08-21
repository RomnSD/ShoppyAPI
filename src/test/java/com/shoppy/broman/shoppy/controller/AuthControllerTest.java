package com.shoppy.broman.shoppy.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;

import com.shoppy.controller.AuthController;
import com.shoppy.controller.dto.LoginCredentials;
import com.shoppy.exception.handler.APIExceptionHandler;
import com.shoppy.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;

import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

class AuthControllerTest {

    private final AuthService authService = Mockito.spy(AuthService.class);
    private MockMvc mockMvc;

    private static final String URL_LOGIN = "/api/v1/auth/login/";
    private static final String URL_REFRESH_TOKEN = "/api/v1/auth/refresh/";
    private static final String URL_LOGOUT = "/api/v1/auth/logout/";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new AuthController(authService)).setControllerAdvice(new APIExceptionHandler()
        ).build();
    }

    @Test
    @DisplayName("Should success when logging with valid credentials")
    void whenLoggingWithValidCredentials_thenSuccess() throws Exception {
        String content = getCredentials();
        AccessTokenResponse token = new AccessTokenResponse();
        token.setSessionState("test");

        Mockito.doReturn(token).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty(),
                        MockMvcResultMatchers.jsonPath("$.session_state").value(token.getSessionState())
                );
    }

    @Test
    @DisplayName("Should fail when logging with invalid credentials")
    void whenLoggingWithInvalidCredentials_thenFail() throws Exception {
        String content = getCredentials();

        Mockito.doThrow(
                new HttpResponseException("", HttpStatus.UNAUTHORIZED.value(), "", null)
        ).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isUnauthorized(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Incorrect username or password")
                );
    }

    @Test
    @DisplayName("Should fail when logging and something unexpected happens")
    void whenLoggingAndSomethingUnexpectedHappens_thenFail() throws Exception {
        String content = getCredentials();

        Mockito.doThrow(
                new HttpResponseException("", HttpStatus.I_AM_A_TEAPOT.value(), "", null)
        ).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isServiceUnavailable(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Log-in service not available")
                );
    }

    @Test
    @DisplayName("Should success when refreshing a valid refresh token")
    void whenRefreshingWithValidToken_thenSuccess() throws Exception {
        AccessTokenResponse token = new AccessTokenResponse();
        token.setSessionState("test");

        Mockito.doReturn(token).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_REFRESH_TOKEN + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty(),
                        MockMvcResultMatchers.jsonPath("$.session_state").value(token.getSessionState())
                );
    }

    @Test
    @DisplayName("Should fail when refreshing with an invalid refresh token")
    void whenRefreshingWithInvalidToken_thenFail() throws Exception {
        Mockito.doThrow(
                new HttpResponseException("", HttpStatus.BAD_REQUEST.value(), "", null)
        ).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_REFRESH_TOKEN + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Refresh token is not valid, try to log-in again")
                );
    }

    @Test
    @DisplayName("Should fail when refreshing token and something unexpected happens")
    void whenRefreshingAndSomethingUnexpectedHappens_thenFail() throws Exception {
        Mockito.doThrow(
                new HttpResponseException("", HttpStatus.I_AM_A_TEAPOT.value(), "", null)
        ).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_REFRESH_TOKEN + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isServiceUnavailable(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Refresh token service not available")
                );
    }

    @Test
    @DisplayName("Should success when logging out with valid refresh token")
    void whenLoggingOutWithValidToken_thenSuccess() throws Exception {
        Mockito.doReturn(null).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_LOGOUT + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail when logging out with an invalid refresh token")
    void whenLoggingOutWithInvalidToken_thenFail() throws Exception {
        Mockito.doThrow(
                new HttpResponseException("", HttpStatus.BAD_REQUEST.value(), "", null)
        ).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_LOGOUT + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Refresh token is not valid")
                );
    }

    @Test
    @DisplayName("Should fail when logging out and something unexpected happens")
    void whenLoggingOutAndSomethingUnexpectedHappens_thenFail() throws Exception {
        Mockito.doThrow(
                new HttpResponseException("", HttpStatus.I_AM_A_TEAPOT.value(), "", null)
        ).when(authService).sendRequest(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_LOGOUT + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isServiceUnavailable(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Logout service not available")
                );
    }

    private String getCredentials() throws Exception {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setUsername("username");
        credentials.setPassword("password");
        return new JsonMapper().writeValueAsString(credentials);
    }

}