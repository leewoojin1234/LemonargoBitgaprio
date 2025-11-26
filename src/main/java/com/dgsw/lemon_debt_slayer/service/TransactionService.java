package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.dto.transaction.SellLemonRequest;
import com.dgsw.lemon_debt_slayer.dto.transaction.StatisticsResponse;
import com.dgsw.lemon_debt_slayer.dto.transaction.TransactionResponse;

import java.util.List;

public interface TransactionService {

    /**
     * 레몬 판매
     */
    TransactionResponse sellLemons(String userId, SellLemonRequest request);

    /**
     * 거래 내역 조회
     */
    List<TransactionResponse> getTransactionHistory(String userId);

    /**
     * 수익 통계 조회
     */
    StatisticsResponse getStatistics(String userId);
}