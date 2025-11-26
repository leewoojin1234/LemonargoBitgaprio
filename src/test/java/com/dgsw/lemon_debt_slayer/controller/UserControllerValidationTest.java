package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.dto.user.CreateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.user.UpdateUserRequest;
import com.dgsw.lemon_debt_slayer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerValidationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("사용자 생성 시 userId가 비어있으면 실패")
    @Test
    void whenCreateUserWithBlankUserId_thenFail() throws Exception {
        // given
        final String url = "/api/users";
        final CreateUserRequest userRequest = new CreateUserRequest("");
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
    }

    @DisplayName("사용자 정보 수정 시 currentMoney가 음수이면 실패")
    @Test
    void whenUpdateUserWithNegativeMoney_thenFail() throws Exception {
        // given
        final String url = "/api/users/some-user";
        final UpdateUserRequest userRequest = new UpdateUserRequest(-100L, null);
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
    }

    @DisplayName("사용자 정보 수정 시 totalDebt가 음수이면 실패")
    @Test
    void whenUpdateUserWithNegativeDebt_thenFail() throws Exception {
        // given
        final String url = "/api/users/some-user";
        final UpdateUserRequest userRequest = new UpdateUserRequest(null, -1000L);
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
    }

    @DisplayName("존재하지 않는 userId로 사용자 조회 시 실패")
    @Test
    void whenFindUserByNonExistentUserId_thenFail() throws Exception {
        // given
        final String nonExistentUserId = "nonexistent@test.com";
        final String url = "/api/users/" + nonExistentUserId;

        // when
        ResultActions result = mockMvc.perform(get(url));

        // then
        result.andExpect(status().isNotFound());
    }

    @DisplayName("존재하지 않는 userId로 사용자 수정 시 실패")
    @Test
    void whenUpdateUserByNonExistentUserId_thenFail() throws Exception {
        // given
        final String nonExistentUserId = "nonexistent@test.com";
        final String url = "/api/users/" + nonExistentUserId;
        final UpdateUserRequest userRequest = new UpdateUserRequest(100L, 1000L);
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound());
    }

    @DisplayName("존재하지 않는 userId로 사용자 삭제 시 실패")
    @Test
    void whenDeleteUserByNonExistentUserId_thenFail() throws Exception {
        // given
        final String nonExistentUserId = "nonexistent@test.com";
        final String url = "/api/users/" + nonExistentUserId;

        // when
        ResultActions result = mockMvc.perform(delete(url));

        // then
        result.andExpect(status().isNotFound());
    }
}
