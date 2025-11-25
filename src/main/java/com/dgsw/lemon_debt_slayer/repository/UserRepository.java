package com.dgsw.lemon_debt_slayer.repository;

import com.dgsw.lemon_debt_slayer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
