package com.dgsw.lemon_debt_slayer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lemon_trees")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LemonTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false)
    private LocalDateTime lastHarvestTime;

    @Column(nullable = false)
    private Integer productionRate; // 레몬 생성 주기 (초 단위)

    @Column(nullable = false)
    private Integer currentLemons; // 현재 쌓인 레몬 개수

    @Column(nullable = false)
    private Long playerId; // 플레이어 ID

    // 도메인 로직: 레몬 추가
    public void addLemon() {
        this.currentLemons++;
    }

    // 도메인 로직: 레몬 수확
    public int harvestLemons() {
        int harvested = this.currentLemons;
        this.currentLemons = 0;
        this.lastHarvestTime = LocalDateTime.now();
        return harvested;
    }

    // 도메인 로직: 레몬 생성 가능 여부 확인
    public boolean canProduceLemon(LocalDateTime currentTime) {
        LocalDateTime nextProductionTime = this.lastHarvestTime.plusSeconds(this.productionRate);
        return currentTime.isAfter(nextProductionTime) || currentTime.isEqual(nextProductionTime);
    }

    // 도메인 로직: 생성 가능한 레몬 개수 계산
    public int calculateProducibleLemons(LocalDateTime currentTime) {
        long secondsPassed = java.time.Duration.between(this.lastHarvestTime, currentTime).getSeconds();
        return (int) (secondsPassed / this.productionRate);
    }
}