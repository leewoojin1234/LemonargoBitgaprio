package com.dgsw.lemon_debt_slayer.repository;

import com.dgsw.lemon_debt_slayer.domain.Transaction;
import com.dgsw.lemon_debt_slayer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 특정 사용자의 모든 거래 내역 조회
    List<Transaction> findByUserOrderByCreateAtDesc(User user);

    // 통계를 위한 쿼리들
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT SUM(t.lemonCount) FROM Transaction t WHERE t.user = :user")
    Long sumLemonCountByUser(@Param("user") User user);

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.user = :user")
    Long sumTotalAmountByUser(@Param("user") User user);

    @Query("SELECT AVG(t.pricePerLemon) FROM Transaction t WHERE t.user = :user")
    Long avgPricePerLemonByUser(@Param("user") User user);
}