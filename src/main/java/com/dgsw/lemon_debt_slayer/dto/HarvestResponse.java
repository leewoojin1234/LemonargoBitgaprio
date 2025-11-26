package com.dgsw.lemon_debt_slayer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestResponse {
    private Integer harvestedLemons;
    private String message;
}
