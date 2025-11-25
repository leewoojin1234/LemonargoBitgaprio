package com.dgsw.lemon_debt_slayer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter // For update operations
@Table(name = "users") // "user" is a reserved keyword in H2
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "current_money", nullable = false)
    private Long currentMoney;

    @Column(name = "total_debt", nullable = false)
    private Long totalDebt;

    @Builder
    public User(String userId, Long currentMoney, Long totalDebt) {
        this.userId = userId;
        this.currentMoney = currentMoney;
        this.totalDebt = totalDebt;
    }

    public void update(String userId, Long currentMoney, Long totalDebt) {
        this.userId = userId;
        this.currentMoney = currentMoney;
        this.totalDebt = totalDebt;
    }
}
