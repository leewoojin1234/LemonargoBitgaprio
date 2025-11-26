package com.dgsw.lemon_debt_slayer.dto.farm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestRequest {
    private Integer x;
    private Integer y;
    private Long playerId;
}