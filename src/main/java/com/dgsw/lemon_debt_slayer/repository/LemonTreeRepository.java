package com.dgsw.lemon_debt_slayer.repository;

import com.dgsw.lemon_debt_slayer.domain.LemonTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository: 데이터베이스 접근만 담당
 * 순수하게 CRUD 작업만 수행
 */
@Repository
public interface LemonTreeRepository extends JpaRepository<LemonTree, Long> {

    // 특정 플레이어의 모든 나무 조회
    List<LemonTree> findByPlayerId(Long playerId);

    // 특정 좌표의 나무 조회
    Optional<LemonTree> findByPlayerIdAndXAndY(Long playerId, Integer x, Integer y);

    // 특정 좌표에 나무가 존재하는지 확인
    boolean existsByPlayerIdAndXAndY(Long playerId, Integer x, Integer y);
}