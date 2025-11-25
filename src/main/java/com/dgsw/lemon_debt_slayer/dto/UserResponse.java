package com.dgsw.lemon_debt_slayer.dto;

import com.dgsw.lemon_debt_slayer.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserResponse {
    private Long id;
    private String userId;
    private Long currentMoney;
    private Long totalDebt;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.currentMoney = user.getCurrentMoney();
        this.totalDebt = user.getTotalDebt();
        this.createAt = user.getCreateAt();
        this.updateAt = user.getUpdateAt();
    }
}
