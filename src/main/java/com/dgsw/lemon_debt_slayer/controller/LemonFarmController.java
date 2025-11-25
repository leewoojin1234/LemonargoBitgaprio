package com.dgsw.lemon_debt_slayer.controller;
import com.dgsw.lemon_debt_slayer.domain.LemonTree;
import com.dgsw.lemon_debt_slayer.service.LemonFarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/farms")
public class LemonFarmController {

    private final LemonFarmService lemonFarmService;

    // 1. 레몬 나무 구매
    @PostMapping("/trees")
    public ResponseEntity<LemonTree> buyTree(@RequestParam Long userId,
                                             @RequestParam String treeType) {
        LemonTree newTree = lemonFarmService.buyTree(userId, treeType);
        return ResponseEntity.ok(newTree);
    }

    // 2. 레몬 수확
    @PutMapping("/harvest")
    public ResponseEntity<Integer> harvestLemons(@RequestParam Long userId, long treePositionX, long treePositionY) {
        int totalLemons = lemonFarmService.harvestLemons(userId, treePositionX, treePositionY);
        return ResponseEntity.ok(totalLemons);
    }

    // 3. 농장 상태 조회
    @GetMapping("/{userId}")
    public ResponseEntity<String> getFarmStatus(@PathVariable Long userId) {
        String FarmTrees = lemonFarmService.getFarmStatus(userId);
        return ResponseEntity.ok(FarmTrees);
    }

    // 4. 나무 제거/판매
    @DeleteMapping("/trees/{treeId}")
    public ResponseEntity<Void> deleteTree(@RequestParam Long userId,
                                           @PathVariable long x,
                                           @PathVariable long y) {
        lemonFarmService.deleteTree(userId, x, y);
        return ResponseEntity.noContent().build();
    }
}
