package com.dgsw.lemon_debt_slayer.service;


import com.dgsw.lemon_debt_slayer.dto.CreateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.UpdateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse findUserByUserId(String userId);
    UserResponse updateUser(String userId, UpdateUserRequest request);
    void deleteUser(String userId);
}
