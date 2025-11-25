package com.dgsw.lemon_debt_slayer.repository;

import com.dgsw.lemon_debt_slayer.domain.DebtHistory;
import com.dgsw.lemon_debt_slayer.domain.LemonTree;
import com.dgsw.lemon_debt_slayer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LemonFarmRepository extends JpaRepository<LemonTree, Long> {
    List<LemonTree> findByUser(LemonTree lemonTree);
}
