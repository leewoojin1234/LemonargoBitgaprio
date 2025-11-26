package com.dgsw.lemon_debt_slayer.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SellLemonRequest {

    @NotNull(message = "판매할 레몬 개수를 입력해주세요.")
    @Positive(message = "판매할 레몬 개수는 0보다 커야 합니다.")
    private Long lemonCount;
}