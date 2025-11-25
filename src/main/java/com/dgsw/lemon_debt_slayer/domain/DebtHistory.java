package com.dgsw.lemon_debt_slayer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "debt_history")
public class DebtHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "repay_amount", nullable = false)
    private Long repayAmount;

    @Column(name = "remaining_debt", nullable = false)
    private Long remainingDebt;

    @Builder
    public DebtHistory(User user, Long repayAmount, Long remainingDebt) {
        this.user = user;
        this.repayAmount = repayAmount;
        this.remainingDebt = remainingDebt;
    }
}
