package com.dgsw.lemon_debt_slayer.dto.user;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserRequest {

    @PositiveOrZero(message = "현재 보유 금액은 0 이상이어야 합니다.")
    private Long currentMoney;

    @PositiveOrZero(message = "총 빚은 0 이상이어야 합니다.")
    private Long totalDebt;
}
