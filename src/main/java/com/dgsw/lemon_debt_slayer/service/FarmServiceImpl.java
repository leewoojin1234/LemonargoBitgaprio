package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.DebtHistory;
import com.dgsw.lemon_debt_slayer.domain.LemonTree;
import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.*;
import com.dgsw.lemon_debt_slayer.repository.LemonTreeRepository;
import com.dgsw.lemon_debt_slayer.repository.UserRepository;
import com.dgsw.lemon_debt_slayer.service.FarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service Implementation: 실제 비즈니스 로직 구현
 * - 유효성 검증
 * - 비즈니스 규칙 적용
 * - Repository 호출
 * - DTO 변환
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FarmServiceImpl implements FarmService {

    private final LemonTreeRepository lemonTreeRepository;
    private final UserRepository userRepository;

    private static final int FARM_WIDTH = 10;
    private static final int FARM_HEIGHT = 10;
    private static final int DEFAULT_PRODUCTION_RATE = 60;

    private static final long treePrice = 10000;


    /**
     * 비즈니스 로직: 나무 구매
     * 1. 좌표 유효성 검증
     * 2. 중복 확인
     * 3. 나무 생성 및 저장
     * 4. DTO 변환 후 반환
     */
    @Override
    @Transactional
    public TreeResponse purchaseTree(TreePurchaseRequest request) {
        // 1. 비즈니스 규칙: 좌표 유효성 검증
        validateCoordinates(request.getX(), request.getY());

        // 2. 비즈니스 규칙: 중복 확인
        if (lemonTreeRepository.existsByPlayerIdAndXAndY(
                request.getPlayerId(), request.getX(), request.getY())) {
            throw new IllegalStateException("해당 위치에 이미 나무가 존재합니다.");
        }

        // 3. 도메인 객체 생성
        LemonTree newTree = LemonTree.builder()
                .x(request.getX())
                .y(request.getY())
                .playerId(request.getPlayerId())
                .lastHarvestTime(LocalDateTime.now())
                .productionRate(DEFAULT_PRODUCTION_RATE)
                .currentLemons(0)
                .build();

        // 4. Repository를 통한 저장
        LemonTree savedTree = lemonTreeRepository.save(newTree);


        User user = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + request.getPlayerId()));

        Long amount = treePrice;

        if (user.getCurrentMoney() < amount) {
            throw new IllegalArgumentException("Not enough money to repay debt.");
        }
        if (user.getTotalDebt() < amount) {
            throw new IllegalArgumentException("Repayment amount exceeds total debt.");
        }

        user.setCurrentMoney(user.getCurrentMoney() - amount);
        user.setTotalDebt(user.getTotalDebt() - amount);

        DebtHistory debtHistory = DebtHistory.builder()
                .user(user)
                .repayAmount(amount)
                .remainingDebt(user.getTotalDebt())
                .build();


        log.info("나무 구매 완료 - PlayerId: {}, 좌표: ({}, {})",
                request.getPlayerId(), request.getX(), request.getY());

        // 5. DTO 변환
        return convertToTreeResponse(savedTree);
    }

    /**
     * 비즈니스 로직: 레몬 수확
     * 1. 나무 존재 확인
     * 2. 도메인 로직 실행 (수확)
     * 3. 변경사항 저장
     * 4. DTO 반환
     */
    @Override
    @Transactional
    public HarvestResponse harvestLemons(HarvestRequest request) {
        // 1. 나무 조회
        LemonTree tree = lemonTreeRepository
                .findByPlayerIdAndXAndY(request.getPlayerId(), request.getX(), request.getY())
                .orElseThrow(() -> new IllegalArgumentException("해당 위치에 나무가 없습니다."));

        // 2. 도메인 로직 실행
        int harvested = tree.harvestLemons();

        // 3. 변경사항 저장
        lemonTreeRepository.save(tree);

        log.info("레몬 수확 완료 - PlayerId: {}, 좌표: ({}, {}), 수확량: {}",
                request.getPlayerId(), request.getX(), request.getY(), harvested);

//        UserRepository userRepository = null;
        User user = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + request.getPlayerId()));

        user.setCurrntLemonCount(user.getCurrntLemonCount() + harvested);

        // 4. DTO 변환
        return HarvestResponse.builder()
                .harvestedLemons(harvested)
                .message(harvested > 0 ?
                        harvested + "개의 레몬을 수확했습니다!" :
                        "수확할 레몬이 없습니다.")
                .build();
    }

    /**
     * 비즈니스 로직: 농장 조회 및 시각화
     * 1. 모든 나무 조회
     * 2. 2차원 배열로 변환
     * 3. 문자열로 포맷팅
     */
    @Override
    @Transactional(readOnly = true)
    public FarmViewResponse viewFarm(Long playerId) {
        // 1. 플레이어의 모든 나무 조회
        List<LemonTree> trees = lemonTreeRepository.findByPlayerId(playerId);

        // 2. 비즈니스 로직: 2차원 배열 생성 및 시각화
        String[][] farm = createFarmGrid(trees);

        // 3. 통계 계산
        int totalLemons = trees.stream()
                .mapToInt(LemonTree::getCurrentLemons)
                .sum();

        // 4. 문자열 포맷팅
        String farmMap = formatFarmGrid(farm, trees.size(), totalLemons);

        return FarmViewResponse.builder()
                .farmMap(farmMap)
                .totalTrees(trees.size())
                .totalLemons(totalLemons)
                .build();
    }

    /**
     * 비즈니스 로직: 나무 제거
     * 1. 나무 존재 확인
     * 2. 삭제
     */
    @Override
    @Transactional
    public void removeTree(Integer x, Integer y, Long playerId) {
        // 1. 나무 조회
        LemonTree tree = lemonTreeRepository
                .findByPlayerIdAndXAndY(playerId, x, y)
                .orElseThrow(() -> new IllegalArgumentException("해당 위치에 나무가 없습니다."));

        // 2. 삭제
        lemonTreeRepository.delete(tree);

        log.info("나무 제거 완료 - PlayerId: {}, 좌표: ({}, {})", playerId, x, y);
    }

    /**
     * 비즈니스 로직: 스케줄러를 위한 레몬 생성
     * 1. 모든 나무 조회
     * 2. 각 나무의 생산 가능 여부 확인
     * 3. 레몬 생성
     */
    @Override
    @Transactional
    @Scheduled(fixedRate = 10000) // 10초마다 자동 실행
    public void produceLemons() {
        List<LemonTree> allTrees = lemonTreeRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        int producedCount = 0;

        for (LemonTree tree : allTrees) {
            // 도메인 로직: 생성 가능 여부 확인
            if (tree.canProduceLemon(now)) {
                // 도메인 로직: 생성 가능한 레몬 개수 계산
                int lemonsToAdd = tree.calculateProducibleLemons(now);

                if (lemonsToAdd > 0) {
                    // 도메인 로직: 레몬 추가
                    for (int i = 0; i < lemonsToAdd; i++) {
                        tree.addLemon();
                    }
                    producedCount += lemonsToAdd;
                }
            }
        }

        if (producedCount > 0) {
            log.info("레몬 생산 완료 - 총 {}개 생성", producedCount);

            }
    }

    // ===== Private Helper Methods (내부 비즈니스 로직) =====

    private void validateCoordinates(Integer x, Integer y) {
        if (x < 0 || x >= FARM_WIDTH || y < 0 || y >= FARM_HEIGHT) {
            throw new IllegalArgumentException(
                    String.format("유효하지 않은 좌표입니다. (0-%d, 0-%d)",
                            FARM_WIDTH - 1, FARM_HEIGHT - 1));
        }
    }

    private String[][] createFarmGrid(List<LemonTree> trees) {
        String[][] farm = new String[FARM_HEIGHT][FARM_WIDTH];

        // 빈 공간 초기화
        for (int i = 0; i < FARM_HEIGHT; i++) {
            for (int j = 0; j < FARM_WIDTH; j++) {
                farm[i][j] = "[ ]";
            }
        }

        // 나무 위치에 레몬 개수 표시
        for (LemonTree tree : trees) {
            farm[tree.getY()][tree.getX()] = "[" + tree.getCurrentLemons() + "]";
        }

        return farm;
    }

    private String formatFarmGrid(String[][] farm, int totalTrees, int totalLemons) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== 농장 현황 ===\n");
        sb.append("  ");

        // X 좌표 헤더
        for (int i = 0; i < FARM_WIDTH; i++) {
            sb.append(String.format("%3d", i));
        }
        sb.append("\n");

        // 농장 그리드
        for (int i = 0; i < FARM_HEIGHT; i++) {
            sb.append(String.format("%2d ", i)); // Y 좌표
            for (int j = 0; j < FARM_WIDTH; j++) {
                sb.append(farm[i][j]);
            }
            sb.append("\n");
        }

        // 통계
        sb.append("\n총 나무: ").append(totalTrees).append("그루");
        sb.append(" | 총 레몬: ").append(totalLemons).append("개\n");

        return sb.toString();
    }

    private TreeResponse convertToTreeResponse(LemonTree tree) {
        return TreeResponse.builder()
                .id(tree.getId())
                .x(tree.getX())
                .y(tree.getY())
                .lastHarvestTime(tree.getLastHarvestTime())
                .productionRate(tree.getProductionRate())
                .currentLemons(tree.getCurrentLemons())
                .build();
    }

}