package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.LemonTree;
import org.springframework.stereotype.Service;

@Service
public class LemonFarmServiceImpl implements LemonFarmService {
    @Override
    public LemonTree buyTree(Long userId, String treeType) {
        return null;
    }

    @Override
    public int harvestLemons(Long userId, long x, long y) {
        return 0;
    }

    @Override
    public void setFarmStatus() {

    }

    @Override
    public String getFarmStatus(Long userId) {
        return null;
    }

    @Override
    public void deleteTree(Long userId, long x, long y) {

    }
}
