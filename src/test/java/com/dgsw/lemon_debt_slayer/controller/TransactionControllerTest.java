package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.transaction.SellLemonRequest;
import com.dgsw.lemon_debt_slayer.repository.TransactionRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    private static final String USER_ID = "transaction_test@test.com";
    private static final Long INITIAL_MONEY = 10_000L;
    private static final Long INITIAL_DEBT = 1_000_000L;
    private static final Long INITIAL_LEMON_COUNT = 100L;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 사용자 생성
        userRepository.save(User.builder()
                .userId(USER_ID)
                .currentMoney(INITIAL_MONEY)
                .totalDebt(INITIAL_DEBT)
                .currntLemonCount(INITIAL_LEMON_COUNT)
                .build());
    }

    @DisplayName("레몬 판매 성공 테스트")
    @Test
    void sellLemons_success() throws Exception {
        // given
        final String url = "/api/transactions/" + USER_ID + "/sell";
        final Long lemonCount = 10L;
        final SellLemonRequest request = new SellLemonRequest(lemonCount);
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.lemonCount").value(lemonCount))
                .andExpect(jsonPath("$.totalAmount").value(lemonCount * 100)) // 100원 per lemon
                .andExpect(jsonPath("$.pricePerLemon").value(100))
                .andExpect(jsonPath("$.transactionType").value("SELL"));

        // 사용자 정보 확인
        User user = userRepository.findByUserId(USER_ID).orElseThrow();
        assertThat(user.getCurrntLemonCount()).isEqualTo(INITIAL_LEMON_COUNT - lemonCount);
        assertThat(user.getCurrentMoney()).isEqualTo(INITIAL_MONEY + (lemonCount * 100));
    }

    @DisplayName("레몬 판매 실패 - 레몬 부족")
    @Test
    void sellLemons_fail_notEnoughLemons() throws Exception {
        // given
        final String url = "/api/transactions/" + USER_ID + "/sell";
        final Long lemonCount = INITIAL_LEMON_COUNT + 10L; // 보유량보다 많은 개수
        final SellLemonRequest request = new SellLemonRequest(lemonCount);
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());

        // 사용자 정보가 변경되지 않았는지 확인
        User user = userRepository.findByUserId(USER_ID).orElseThrow();
        assertThat(user.getCurrntLemonCount()).isEqualTo(INITIAL_LEMON_COUNT);
        assertThat(user.getCurrentMoney()).isEqualTo(INITIAL_MONEY);
    }

    @DisplayName("레몬 판매 실패 - 유효하지 않은 개수")
    @Test
    void sellLemons_fail_invalidCount() throws Exception {
        // given
        final String url = "/api/transactions/" + USER_ID + "/sell";
        final Long lemonCount = -5L; // 음수
        final SellLemonRequest request = new SellLemonRequest(lemonCount);
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("거래 내역 조회 테스트")
    @Test
    void getTransactionHistory_success() throws Exception {
        // given
        // 먼저 거래를 생성
        final String sellUrl = "/api/transactions/" + USER_ID + "/sell";
        final SellLemonRequest request1 = new SellLemonRequest(5L);
        mockMvc.perform(post(sellUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        final SellLemonRequest request2 = new SellLemonRequest(10L);
        mockMvc.perform(post(sellUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        // when
        final String historyUrl = "/api/transactions/" + USER_ID + "/history";
        ResultActions result = mockMvc.perform(get(historyUrl));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].lemonCount").value(10)) // 최신순이므로 두 번째 거래가 먼저
                .andExpect(jsonPath("$[1].lemonCount").value(5));
    }

    @DisplayName("수익 통계 조회 테스트")
    @Test
    void getStatistics_success() throws Exception {
        // given
        // 먼저 거래를 생성
        final String sellUrl = "/api/transactions/" + USER_ID + "/sell";
        final SellLemonRequest request1 = new SellLemonRequest(5L);
        mockMvc.perform(post(sellUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        final SellLemonRequest request2 = new SellLemonRequest(10L);
        mockMvc.perform(post(sellUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        // when
        final String statisticsUrl = "/api/transactions/" + USER_ID + "/statistics";
        ResultActions result = mockMvc.perform(get(statisticsUrl));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransactionCount").value(2))
                .andExpect(jsonPath("$.totalLemonsSold").value(15))
                .andExpect(jsonPath("$.totalRevenue").value(1500))
                .andExpect(jsonPath("$.averagePricePerLemon").value(100));
    }

    @DisplayName("거래 내역 없을 때 통계 조회")
    @Test
    void getStatistics_noTransactions() throws Exception {
        // given
        final String statisticsUrl = "/api/transactions/" + USER_ID + "/statistics";

        // when
        ResultActions result = mockMvc.perform(get(statisticsUrl));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransactionCount").value(0))
                .andExpect(jsonPath("$.totalLemonsSold").value(0))
                .andExpect(jsonPath("$.totalRevenue").value(0))
                .andExpect(jsonPath("$.averagePricePerLemon").value(0));
    }

    @DisplayName("존재하지 않는 사용자로 판매 시도")
    @Test
    void sellLemons_fail_userNotFound() throws Exception {
        // given
        final String url = "/api/transactions/nonexistent@test.com/sell";
        final SellLemonRequest request = new SellLemonRequest(10L);
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest());
    }
}