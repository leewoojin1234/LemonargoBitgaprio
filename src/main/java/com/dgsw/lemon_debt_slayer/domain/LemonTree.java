package com.dgsw.lemon_debt_slayer.domain;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Vector;

@Entity
@Table(name = "lemon_tree")
public class LemonTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // 나무 등급(예: NORMAL, RARE, EPIC 같은 문자열)
    private String treeType;

    // 구매 가격
    private int purchasePrice;

    // 시간당 생산량
    private int productionRate;

    // 누적 생산시간
    private long currentProductionTime = 1;

    private long treePositionX;
    private long treePositionY;

    protected LemonTree() {}

    public LemonTree(Long userId,/* String treeType, int purchasePrice, int productionRate,*/ long treePositionX, long treePositionY) {
        this.userId = userId;
        this.treeType = treeType;
        this.purchasePrice = purchasePrice;
        this.productionRate = productionRate;

        this.treePositionX = treePositionX;
        this.treePositionY = treePositionY;
        currentProductionTime = 0;
    }

//    /**
//     * 경과 시간 기반 생산량 계산
//     */
//    public int harvest() {
//        if (!isActive) return 0;
//
//        LocalDateTime now = LocalDateTime.now();
//        long hours = java.time.Duration.between(lastHarvestTime, now).toHours();
//
//        if (hours <= 0) return 0;
//
//        int lemons = (int) (hours * productionRate);
//        lastHarvestTime = now;
//
//        return lemons;
//    }

    // getter들 생략 가능 (필요하면 적어달라 해)
}
