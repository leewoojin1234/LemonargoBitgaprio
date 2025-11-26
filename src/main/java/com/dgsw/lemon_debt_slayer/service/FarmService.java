package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.dto.farm.*;

/**
 * Service Interface: 비즈니스 로직의 계약(Contract) 정의
 * 어떤 기능을 제공할지만 선언
 */
public interface FarmService {

    /**
     * 레몬 나무 구매
     */
    TreeResponse purchaseTree(TreePurchaseRequest request);

    /**
     * 레몬 수확
     */
    HarvestResponse harvestLemons(HarvestRequest request);

    /**
     * 농장 조회 (시각화)
     */
    FarmViewResponse viewFarm(Long playerId);

    /**
     * 나무 제거
     */
    void removeTree(Integer x, Integer y, Long playerId);

    /**
     * 모든 나무에 레몬 생성 (스케줄러용)
     */
    void produceLemons();
}