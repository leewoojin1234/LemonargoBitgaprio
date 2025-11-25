package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.dto.DebtHistoryResponse;
import com.dgsw.lemon_debt_slayer.dto.RepayDebtRequest;
import com.dgsw.lemon_debt_slayer.service.DebtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/debts")
public class DebtController {

    private final DebtService debtService;

    @PutMapping("/{userId}/repay")
    public ResponseEntity<DebtHistoryResponse> repayDebt(
            @PathVariable String userId,
            @RequestBody @Valid RepayDebtRequest request) {
        // Ensure the userId in path matches the userId in the request body if present, or set it.
        // For simplicity, we'll assume the path variable userId is authoritative.
        DebtHistoryResponse debtHistory = debtService.repayDebt(userId, request);
        return ResponseEntity.ok(debtHistory);
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<DebtHistoryResponse>> getDebtHistory(@PathVariable String userId) {
        List<DebtHistoryResponse> history = debtService.getDebtHistory(userId);
        return ResponseEntity.ok(history);
    }
}
