package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.DebtHistory;
import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.debt.DebtHistoryResponse;
import com.dgsw.lemon_debt_slayer.dto.debt.RepayDebtRequest;
import com.dgsw.lemon_debt_slayer.repository.DebtHistoryRepository;
import com.dgsw.lemon_debt_slayer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DebtServiceImpl implements DebtService {

    private final UserRepository userRepository;
    private final DebtHistoryRepository debtHistoryRepository;

    private static final double INTEREST_RATE_DAILY = 0.001; // 0.1% daily

    @Override
    @Transactional
    public DebtHistoryResponse repayDebt(String userId, RepayDebtRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        Long amount = request.getAmount();

        if (user.getCurrentMoney() < amount) {
            throw new IllegalArgumentException("Not enough money to repay debt.");
        }
        if (user.getTotalDebt() < amount) {
            throw new IllegalArgumentException("Repayment amount exceeds total debt.");
        }

        user.setCurrentMoney(user.getCurrentMoney() - amount);
        user.setTotalDebt(user.getTotalDebt() - amount);

        DebtHistory debtHistory = DebtHistory.builder()
                .user(user)
                .repayAmount(amount)
                .remainingDebt(user.getTotalDebt())
                .build();

        return new DebtHistoryResponse(debtHistoryRepository.save(debtHistory));
    }

    @Override
    public List<DebtHistoryResponse> getDebtHistory(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        return debtHistoryRepository.findByUser(user)
                .stream()
                .map(DebtHistoryResponse::new)
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 * * *") // Run every day at midnight
    @Transactional
    public void calculateDailyInterest() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getTotalDebt() > 0) {
                Long interest = (long) (user.getTotalDebt() * INTEREST_RATE_DAILY);
                user.setTotalDebt(user.getTotalDebt() + interest);
                // Optionally, log this or add to DebtHistory
                // For simplicity, we are just updating totalDebt here.
            }
        }
    }
}
