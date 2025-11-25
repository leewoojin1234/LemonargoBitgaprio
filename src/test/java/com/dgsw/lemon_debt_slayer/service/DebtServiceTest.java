package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.RepayDebtRequest;
import com.dgsw.lemon_debt_slayer.repository.DebtHistoryRepository;
import com.dgsw.lemon_debt_slayer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat; // Import AssertJ for assertThat
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DebtServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DebtHistoryRepository debtHistoryRepository;

    @InjectMocks
    private DebtServiceImpl debtService;

    private User testUser;
    private static final String USER_ID = "test@test.com";
    private static final Long INITIAL_MONEY = 10000L;
    private static final Long INITIAL_DEBT = 1000000L;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(USER_ID)
                .currentMoney(INITIAL_MONEY)
                .totalDebt(INITIAL_DEBT)
                .build();
    }

    @DisplayName("빚 상환 성공 테스트")
    @Test
    void repayDebt_success() {
        // given
        Long repayAmount = 1000L;
        RepayDebtRequest request = new RepayDebtRequest(repayAmount);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));
        when(debtHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved object

        // when
        debtService.repayDebt(USER_ID, request);

        // then
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(debtHistoryRepository, times(1)).save(any());
        // Verify user's money and debt are updated
        assertThat(testUser.getCurrentMoney()).isEqualTo(INITIAL_MONEY - repayAmount);
        assertThat(testUser.getTotalDebt()).isEqualTo(INITIAL_DEBT - repayAmount);
    }

    @DisplayName("빚 상환 실패 테스트 - 유저 없음")
    @Test
    void repayDebt_fail_userNotFound() {
        // given
        Long repayAmount = 1000L;
        RepayDebtRequest request = new RepayDebtRequest(repayAmount);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> debtService.repayDebt(USER_ID, request));
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(debtHistoryRepository, never()).save(any()); // No debt history should be saved
    }

    @DisplayName("빚 상환 실패 테스트 - 잔액 부족")
    @Test
    void repayDebt_fail_insufficientFunds() {
        // given
        Long repayAmount = INITIAL_MONEY + 100L; // More than current money
        RepayDebtRequest request = new RepayDebtRequest(repayAmount);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> debtService.repayDebt(USER_ID, request));
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(debtHistoryRepository, never()).save(any()); // No debt history should be saved
        // Verify user's money and debt remain unchanged
        assertThat(testUser.getCurrentMoney()).isEqualTo(INITIAL_MONEY);
        assertThat(testUser.getTotalDebt()).isEqualTo(INITIAL_DEBT);
    }

    @DisplayName("빚 상환 실패 테스트 - 상환액이 총 빚보다 많음")
    @Test
    void repayDebt_fail_exceedsTotalDebt() {
        // given
        Long repayAmount = INITIAL_DEBT + 100L; // More than total debt
        RepayDebtRequest request = new RepayDebtRequest(repayAmount);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testUser));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> debtService.repayDebt(USER_ID, request));
        verify(userRepository, times(1)).findByUserId(USER_ID);
        verify(debtHistoryRepository, never()).save(any()); // No debt history should be saved
        // Verify user's money and debt remain unchanged
        assertThat(testUser.getCurrentMoney()).isEqualTo(INITIAL_MONEY);
        assertThat(testUser.getTotalDebt()).isEqualTo(INITIAL_DEBT);
    }
}
