package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.LemonTree;

public interface LemonFarmService {

//    void BuyLemonTree(Long _TreePrice);

    // 레몬 나무 구매
    LemonTree buyTree(Long userId, String treeType);

    // 레몬 수확 (모든 활성 나무 기준 수확)
    int harvestLemons(Long userId, long x, long y);

    void setFarmStatus();

    // 농장 조회
    String getFarmStatus(Long userId);

    // 나무 제거/판매
    void deleteTree(Long userId, long x, long y);

}
