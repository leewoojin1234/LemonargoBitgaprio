package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.Transaction;
import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.transaction.SellLemonRequest;
import com.dgsw.lemon_debt_slayer.dto.transaction.StatisticsResponse;
import com.dgsw.lemon_debt_slayer.dto.transaction.TransactionResponse;
import com.dgsw.lemon_debt_slayer.repository.TransactionRepository;
import com.dgsw.lemon_debt_slayer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private static final Long PRICE_PER_LEMON = 100L; // 레몬 1개당 100원

    @Override
    @Transactional
    public TransactionResponse sellLemons(String userId, SellLemonRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        Long lemonCount = request.getLemonCount();

        // 2. 보유 레몬 개수 확인
        if (user.getCurrntLemonCount() < lemonCount) {
            throw new IllegalArgumentException("Not enough lemons to sell. Current lemons: " + user.getCurrntLemonCount());
        }

        // 3. 판매 금액 계산
        Long totalAmount = lemonCount * PRICE_PER_LEMON;

        // 4. 사용자 정보 업데이트 (레몬 감소, 돈 증가)
        user.setCurrntLemonCount(user.getCurrntLemonCount() - lemonCount);
        user.setCurrentMoney(user.getCurrentMoney() + totalAmount);

        // 5. 거래 내역 저장
        Transaction transaction = Transaction.builder()
                .user(user)
                .lemonCount(lemonCount)
                .totalAmount(totalAmount)
                .pricePerLemon(PRICE_PER_LEMON)
                .transactionType(Transaction.TransactionType.SELL)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(String userId) {
        // 1. 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // 2. 거래 내역 조회 (최신순)
        return transactionRepository.findByUserOrderByCreateAtDesc(user)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public StatisticsResponse getStatistics(String userId) {
        // 1. 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // 2. 통계 데이터 조회
        Long totalTransactionCount = transactionRepository.countByUser(user);
        Long totalLemonsSold = transactionRepository.sumLemonCountByUser(user);
        Long totalRevenue = transactionRepository.sumTotalAmountByUser(user);
        Long averagePricePerLemon = transactionRepository.avgPricePerLemonByUser(user);

        // 3. null 처리 (거래 내역이 없을 경우)
        if (totalTransactionCount == null) totalTransactionCount = 0L;
        if (totalLemonsSold == null) totalLemonsSold = 0L;
        if (totalRevenue == null) totalRevenue = 0L;
        if (averagePricePerLemon == null) averagePricePerLemon = 0L;

        return StatisticsResponse.builder()
                .totalTransactionCount(totalTransactionCount)
                .totalLemonsSold(totalLemonsSold)
                .totalRevenue(totalRevenue)
                .averagePricePerLemon(averagePricePerLemon)
                .build();
    }
}