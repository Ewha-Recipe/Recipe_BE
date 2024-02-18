package com.example.teamproject.dto;

import com.example.teamproject.domain.constant.RoleType;
import com.example.teamproject.domain.constant.UserStatus;
import com.example.teamproject.entity.User;

import java.time.LocalDateTime;

public record UserDto(
        Long userId,
        String loginId,
        String username,
        String password,
        UserStatus status,
        String email,
        RoleType roleType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // factory method of 선언
    public static UserDto of(Long userId, String loginId, String username, String password, UserStatus status, String email, RoleType roleType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new UserDto(userId, loginId, username, password, status, email, roleType, createdAt, updatedAt);
    }

    // security에서 사용할 팩토리 메서드
    public static UserDto of(String loginId) {
        return new UserDto(
                null, loginId, null, null, null, null, null, null, null
        );
    }

    // User 엔티티를 UserDto로 변환하는 메서드
    public static UserDto fromEntity(User user) {
        return new UserDto(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserStatus(),
                user.getEmail(),
                user.getRoleType(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
