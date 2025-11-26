package com.dgsw.lemon_debt_slayer.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class StatisticsResponse {
    private Long totalTransactionCount;  // 총 거래 횟수
    private Long totalLemonsSold;        // 총 판매한 레몬 개수
    private Long totalRevenue;           // 총 수익
    private Long averagePricePerLemon;   // 레몬당 평균 가격
}