package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.dto.DebtHistoryResponse;
import com.dgsw.lemon_debt_slayer.dto.RepayDebtRequest;

import java.util.List;

public interface DebtService {
    DebtHistoryResponse repayDebt(String userId, RepayDebtRequest request);
    List<DebtHistoryResponse> getDebtHistory(String userId);
}
