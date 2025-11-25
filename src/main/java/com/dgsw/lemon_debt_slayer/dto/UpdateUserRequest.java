package com.dgsw.lemon_debt_slayer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserRequest {
    private String userId;
    private Long currentMoney;
    private Long totalDebt;
}
