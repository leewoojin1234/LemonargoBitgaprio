package com.dgsw.lemon_debt_slayer.scheduler;

import com.dgsw.lemon_debt_slayer.service.FarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler: 정해진 시간마다 작업 실행
 * - Service의 메서드를 호출만 함
 * - 비즈니스 로직은 Service에 위임
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LemonProductionScheduler {

    private final FarmService farmService;

    /**
     * 매 10초마다 실행
     * Service에게 레몬 생성 작업 위임
     */
    @Scheduled(fixedRate = 10000)
    public void scheduleLemonProduction() {
        log.debug("레몬 생성 스케줄러 실행");
        farmService.produceLemons();
    }
}