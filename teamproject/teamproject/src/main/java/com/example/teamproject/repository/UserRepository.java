package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.teamproject.entity.User;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
}
