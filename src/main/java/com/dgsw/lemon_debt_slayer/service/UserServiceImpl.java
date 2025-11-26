package com.dgsw.lemon_debt_slayer.service;

import com.dgsw.lemon_debt_slayer.domain.User;
import com.dgsw.lemon_debt_slayer.dto.user.CreateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.user.UpdateUserRequest;
import com.dgsw.lemon_debt_slayer.dto.user.UserResponse;
import com.dgsw.lemon_debt_slayer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final Long INITIAL_DEBT = 1_000_000L; // 1,000,000 KRW
    private static final Long INITIAL_MONEY = 10_000L;  // 10,000 KRW
    private static final Long INITIAL_LEMONCOUNT = 10_000L;  // 10,000 KRW

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = User.builder()
                .userId(request.getUserId())
                .currentMoney(INITIAL_MONEY)
                .totalDebt(INITIAL_DEBT)
                .currntLemonCount(INITIAL_LEMONCOUNT)
                .build();
        return new UserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse findUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));
        return new UserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // Update fields if provided in the request
        if (request.getCurrentMoney() != null) {
            user.setCurrentMoney(request.getCurrentMoney());
        }
        if (request.getTotalDebt() != null) {
            user.setTotalDebt(request.getTotalDebt());
        }
        // Save is not explicitly called here because @Transactional handles flushing changes
        // But for clarity or if specific update logic is needed, userRepository.save(user) can be called.

        return new UserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));
        userRepository.delete(user);
    }
}
