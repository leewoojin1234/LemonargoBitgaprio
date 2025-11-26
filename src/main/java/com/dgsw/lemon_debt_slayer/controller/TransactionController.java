package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.dto.transaction.SellLemonRequest;
import com.dgsw.lemon_debt_slayer.dto.transaction.StatisticsResponse;
import com.dgsw.lemon_debt_slayer.dto.transaction.TransactionResponse;
import com.dgsw.lemon_debt_slayer.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 레몬 판매
     * POST /api/transactions/{userId}/sell
     */
    @PostMapping("/{userId}/sell")
    public ResponseEntity<TransactionResponse> sellLemons(
            @PathVariable String userId,
            @RequestBody @Valid SellLemonRequest request) {
        TransactionResponse response = transactionService.sellLemons(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 거래 내역 조회
     * GET /api/transactions/{userId}/history
     */
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(@PathVariable String userId) {
        List<TransactionResponse> history = transactionService.getTransactionHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * 수익 통계 조회
     * GET /api/transactions/{userId}/statistics
     */
    @GetMapping("/{userId}/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics(@PathVariable String userId) {
        StatisticsResponse statistics = transactionService.getStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * IllegalArgumentException 처리
     * 비즈니스 로직 검증 실패 시 400 Bad Request 반환
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}