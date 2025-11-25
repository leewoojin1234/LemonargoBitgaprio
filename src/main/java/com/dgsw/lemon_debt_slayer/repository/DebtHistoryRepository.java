package com.dgsw.lemon_debt_slayer.repository;

import com.dgsw.lemon_debt_slayer.domain.DebtHistory;
import com.dgsw.lemon_debt_slayer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtHistoryRepository extends JpaRepository<DebtHistory, Long> {
    List<DebtHistory> findByUser(User user);
}
