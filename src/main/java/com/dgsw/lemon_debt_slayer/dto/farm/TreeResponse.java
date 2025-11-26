package com.dgsw.lemon_debt_slayer.dto.farm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreeResponse {
    private Long id;
    private Integer x;
    private Integer y;
    private LocalDateTime lastHarvestTime;
    private Integer productionRate;
    private Integer currentLemons;
}