package com.dgsw.lemon_debt_slayer.dto;

import com.dgsw.lemon_debt_slayer.domain.DebtHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class DebtHistoryResponse {
    private Long id;
    private String userId;
    private Long repayAmount;
    private Long remainingDebt;
    private LocalDateTime repaidAt;

    public DebtHistoryResponse(DebtHistory debtHistory) {
        this.id = debtHistory.getId();
        this.userId = debtHistory.getUser().getUserId();
        this.repayAmount = debtHistory.getRepayAmount();
        this.remainingDebt = debtHistory.getRemainingDebt();
        this.repaidAt = debtHistory.getCreateAt(); // Assuming createAt from BaseTimeEntity is the repaidAt
    }
}
