package com.dgsw.lemon_debt_slayer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "transactions")
public class Transaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "lemon_count", nullable = false)
    private Long lemonCount;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "price_per_lemon", nullable = false)
    private Long pricePerLemon;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Builder
    public Transaction(User user, Long lemonCount, Long totalAmount, Long pricePerLemon, TransactionType transactionType) {
        this.user = user;
        this.lemonCount = lemonCount;
        this.totalAmount = totalAmount;
        this.pricePerLemon = pricePerLemon;
        this.transactionType = transactionType;
    }

    public enum TransactionType {
        SELL  // 판매
    }
}