package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.Transaction;
import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.transaction.SellLemonRequest;
import com.dgsw.lemon_debt_slayer.dto.transaction.StatisticsResponse;
import com.dgsw.lemon_debt_slayer.dto.transaction.TransactionResponse;
import com.dgsw.lemon_debt_slayer.repository.TransactionRepository;
import com.dgsw.lemon_debt_slayer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private static final String USER_ID = "test@test.com";
    private static final Long INITIAL_MONEY = 10_000L;
    private static final Long INITIAL_DEBT = 1_000_000L;
    private static final Long INITIAL_LEMON_COUNT = 100L;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(USER_ID)
                .currentMoney(INITIAL_MONEY)
                .totalDebt(INITIAL_DEBT)
                .currntLemonCount(INITIAL_LEMON_COUNT)
                .build();
    }

    @DisplayName("레몬 판매 성공 테스트")
    @Test
    void sellLemons_success() {
        // given
        Long lemonCount = 10L;
        SellLemonRequest request = new SellLemonRequest(lemonCount);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            return transaction;
        });

        // when
        TransactionResponse response = transactionService.sellLemons(USER_ID, request);

        // then
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

        // 사용자 정보 확인
        assertThat(testUser.getCurrntLemonCount()).isEqualTo(INITIAL_LEMON_COUNT - lemonCount);
        assertThat(testUser.getCurrentMoney()).isEqualTo(INITIAL_MONEY + (lemonCount * 100));

        // 응답 확인
        assertThat(response.getUserId()).isEqualTo(USER_ID);
        assertThat(response.getLemonCount()).isEqualTo(lemonCount);
        assertThat(response.getTotalAmount()).isEqualTo(lemonCount * 100);
        assertThat(response.getPricePerLemon()).isEqualTo(100L);
    }

    @DisplayName("레몬 판매 실패 - 사용자 없음")
    @Test
    void sellLemons_fail_userNotFound() {
        // given
        Long lemonCount = 10L;
        SellLemonRequest request = new SellLemonRequest(lemonCount);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> transactionService.sellLemons(USER_ID, request));
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(transactionRepository, never()).save(any());
    }

    @DisplayName("레몬 판매 실패 - 레몬 부족")
    @Test
    void sellLemons_fail_notEnoughLemons() {
        // given
        Long lemonCount = INITIAL_LEMON_COUNT + 10L;
        SellLemonRequest request = new SellLemonRequest(lemonCount);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> transactionService.sellLemons(USER_ID, request));
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(transactionRepository, never()).save(any());

        // 사용자 정보가 변경되지 않았는지 확인
        assertThat(testUser.getCurrntLemonCount()).isEqualTo(INITIAL_LEMON_COUNT);
        assertThat(testUser.getCurrentMoney()).isEqualTo(INITIAL_MONEY);
    }

    @DisplayName("거래 내역 조회 성공 테스트")
    @Test
    void getTransactionHistory_success() {
        // given
        Transaction transaction1 = Transaction.builder()
                .user(testUser)
                .lemonCount(10L)
                .totalAmount(1000L)
                .pricePerLemon(100L)
                .transactionType(Transaction.TransactionType.SELL)
                .build();

        Transaction transaction2 = Transaction.builder()
                .user(testUser)
                .lemonCount(5L)
                .totalAmount(500L)
                .pricePerLemon(100L)
                .transactionType(Transaction.TransactionType.SELL)
                .build();

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(transactionRepository.findByUserOrderByCreateAtDesc(testUser)).thenReturn(transactions);

        // when
        List<TransactionResponse> history = transactionService.getTransactionHistory(USER_ID);

        // then
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(transactionRepository, times(1)).findByUserOrderByCreateAtDesc(testUser);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getLemonCount()).isEqualTo(10L);
        assertThat(history.get(1).getLemonCount()).isEqualTo(5L);
    }

    @DisplayName("거래 내역 조회 실패 - 사용자 없음")
    @Test
    void getTransactionHistory_fail_userNotFound() {
        // given
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionHistory(USER_ID));
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(transactionRepository, never()).findByUserOrderByCreateAtDesc(any());
    }

    @DisplayName("통계 조회 성공 테스트")
    @Test
    void getStatistics_success() {
        // given
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(transactionRepository.countByUser(testUser)).thenReturn(5L);
        when(transactionRepository.sumLemonCountByUser(testUser)).thenReturn(50L);
        when(transactionRepository.sumTotalAmountByUser(testUser)).thenReturn(5000L);
        when(transactionRepository.avgPricePerLemonByUser(testUser)).thenReturn(100L);

        // when
        StatisticsResponse statistics = transactionService.getStatistics(USER_ID);

        // then
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(transactionRepository, times(1)).countByUser(testUser);
        verify(transactionRepository, times(1)).sumLemonCountByUser(testUser);
        verify(transactionRepository, times(1)).sumTotalAmountByUser(testUser);
        verify(transactionRepository, times(1)).avgPricePerLemonByUser(testUser);

        assertThat(statistics.getTotalTransactionCount()).isEqualTo(5L);
        assertThat(statistics.getTotalLemonsSold()).isEqualTo(50L);
        assertThat(statistics.getTotalRevenue()).isEqualTo(5000L);
        assertThat(statistics.getAveragePricePerLemon()).isEqualTo(100L);
    }

    @DisplayName("통계 조회 성공 - 거래 내역 없음")
    @Test
    void getStatistics_success_noTransactions() {
        // given
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(transactionRepository.countByUser(testUser)).thenReturn(null);
        when(transactionRepository.sumLemonCountByUser(testUser)).thenReturn(null);
        when(transactionRepository.sumTotalAmountByUser(testUser)).thenReturn(null);
        when(transactionRepository.avgPricePerLemonByUser(testUser)).thenReturn(null);

        // when
        StatisticsResponse statistics = transactionService.getStatistics(USER_ID);

        // then
        assertThat(statistics.getTotalTransactionCount()).isEqualTo(0L);
        assertThat(statistics.getTotalLemonsSold()).isEqualTo(0L);
        assertThat(statistics.getTotalRevenue()).isEqualTo(0L);
        assertThat(statistics.getAveragePricePerLemon()).isEqualTo(0L);
    }

    @DisplayName("통계 조회 실패 - 사용자 없음")
    @Test
    void getStatistics_fail_userNotFound() {
        // given
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> transactionService.getStatistics(USER_ID));
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(transactionRepository, never()).countByUser(any());
    }
}