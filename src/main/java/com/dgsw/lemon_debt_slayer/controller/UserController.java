package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.dto.CreateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.UpdateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.UserResponse;
import com.dgsw.lemon_debt_slayer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/api/users/{userId}")
    public ResponseEntity<UserResponse> findUserByUserId(@PathVariable String userId) {
        UserResponse user = userService.findUserByUserId(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/api/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/api/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
