package com.dgsw.lemon_debt_slayer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmViewResponse {
    private String farmMap; // 2차원 배열을 문자열로 표현
    private int totalTrees;
    private int totalLemons;
}
