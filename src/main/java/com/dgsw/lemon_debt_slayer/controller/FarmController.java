package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.dto.farm.*;
import com.dgsw.lemon_debt_slayer.service.FarmService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

/**
 * Controller: HTTP 요청/응답 처리만 담당
 * - 요청 받기
 * - Service 호출
 * - 응답 반환
 * 비즈니스 로직은 절대 작성하지 않음!
 */
@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    /**
     * POST /api/farms/trees
     * 요청: TreePurchaseRequest (x, y, playerId)
     * 응답: TreeResponse (201 Created)
     */
    @PostMapping("/trees")
    public ResponseEntity<TreeResponse> purchaseTree(@RequestBody TreePurchaseRequest request) {
        TreeResponse response = farmService.purchaseTree(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/farms/harvest
     * 요청: HarvestRequest (x, y, playerId)
     * 응답: HarvestResponse (200 OK)
     */
    @PutMapping("/harvest")
    public ResponseEntity<HarvestResponse> harvestLemons(@RequestBody HarvestRequest request) {
        HarvestResponse response = farmService.harvestLemons(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/farms?playerId=123
     * 응답: FarmViewResponse (200 OK)
     */
    @GetMapping
    public ResponseEntity<FarmViewResponse> viewFarm(@RequestParam Long playerId) {
        FarmViewResponse response = farmService.viewFarm(playerId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/farms/trees/{x}/{y}
     * 요청: TreeRemoveRequest (playerId)
     * 응답: 204 No Content
     */
    @DeleteMapping("/trees/{x}/{y}")
    public ResponseEntity<Void> removeTree(
            @PathVariable Integer x,
            @PathVariable Integer y,
            @RequestBody TreeRemoveRequest request) {
        farmService.removeTree(x, y, request.getPlayerId());
        return ResponseEntity.noContent().build();
    }

    /**
     * IllegalArgumentException 처리
     * 비즈니스 로직 검증 실패 시 400 Bad Request 반환
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 예상치 못한 예외 처리
     * 500 Internal Server Error 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("서버 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 에러 응답 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}