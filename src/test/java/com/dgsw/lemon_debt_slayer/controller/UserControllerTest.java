package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.CreateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.UpdateUserRequest;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    private static final Long INITIAL_DEBT = 1_000_000L;
    private static final Long INITIAL_MONEY = 10_000L;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("새로운 사용자 추가 테스트")
    @Test
    void createUser() throws Exception {
        // given
        final String url = "/api/users";
        final String userId = "test@test.com";
        final CreateUserRequest userRequest = new CreateUserRequest(userId);
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        User savedUser = users.get(0);
        assertThat(savedUser.getUserId()).isEqualTo(userId);
        assertThat(savedUser.getCurrentMoney()).isEqualTo(INITIAL_MONEY);
        assertThat(savedUser.getTotalDebt()).isEqualTo(INITIAL_DEBT);
    }

    @DisplayName("UserId로 사용자 조회 테스트")
    @Test
    void findUserByUserId() throws Exception {
        // given
        final String userId = "test@test.com";
        userRepository.save(User.builder()
                .userId(userId)
                .currentMoney(INITIAL_MONEY)
                .totalDebt(INITIAL_DEBT)
                .currntLemonCount(0L)
                .build());
        final String url = "/api/users/" + userId;

        // when
        final ResultActions resultActions = mockMvc.perform(get(url));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.currentMoney").value(INITIAL_MONEY))
                .andExpect(jsonPath("$.totalDebt").value(INITIAL_DEBT));
    }

    @DisplayName("사용자 정보 수정 테스트")
    @Test
    void updateUser() throws Exception {
        // given
        final String originalUserId = "beforeUpdate@test.com";
        User savedUser = userRepository.save(User.builder()
                .userId(originalUserId)
                .currentMoney(INITIAL_MONEY)
                .totalDebt(INITIAL_DEBT)
                .currntLemonCount(0L)
                .build());

        final String url = "/api/users/" + originalUserId;
        final Long updatedMoney = 20000L;
        final Long updatedDebt = 500000L;
        final UpdateUserRequest updateUserRequest = new UpdateUserRequest(updatedMoney, updatedDebt);
        final String requestBody = objectMapper.writeValueAsString(updateUserRequest);

        // when
        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());

        User updatedUser = userRepository.findByUserId(originalUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        assertThat(updatedUser.getCurrentMoney()).isEqualTo(updatedMoney);
        assertThat(updatedUser.getTotalDebt()).isEqualTo(updatedDebt);
    }

    @DisplayName("사용자 삭제 테스트")
    @Test
    void deleteUser() throws Exception {
        // given
        final String userId = "test@test.com";
        userRepository.save(User.builder()
                .userId(userId)
                .currentMoney(INITIAL_MONEY)
                .totalDebt(INITIAL_DEBT)
                .currntLemonCount(0L)
                .build());

        final String url = "/api/users/" + userId;

        // when
        ResultActions result = mockMvc.perform(delete(url));

        // then
        result.andExpect(status().isNoContent());

        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }
}
