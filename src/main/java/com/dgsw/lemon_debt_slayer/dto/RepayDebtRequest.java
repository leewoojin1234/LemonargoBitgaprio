package com.dgsw.lemon_debt_slayer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RepayDebtRequest {

    @NotNull(message = "상환할 금액을 입력해주세요.")
    @Positive(message = "상환할 금액은 0보다 커야 합니다.")
    private Long amount;
}
