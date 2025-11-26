package com.dgsw.lemon_debt_slayer.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateUserRequest {

    @NotBlank(message = "유저 아이디는 필수입니다.")
    private String userId;
}
