package com.dgsw.lemon_debt_slayer.dto.transaction;

import com.dgsw.lemon_debt_slayer.domain.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class TransactionResponse {
    private Long id;
    private String userId;
    private Long lemonCount;
    private Long totalAmount;
    private Long pricePerLemon;
    private String transactionType;
    private LocalDateTime transactionAt;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.userId = transaction.getUser().getUserId();
        this.lemonCount = transaction.getLemonCount();
        this.totalAmount = transaction.getTotalAmount();
        this.pricePerLemon = transaction.getPricePerLemon();
        this.transactionType = transaction.getTransactionType().name();
        this.transactionAt = transaction.getCreateAt();
    }
}