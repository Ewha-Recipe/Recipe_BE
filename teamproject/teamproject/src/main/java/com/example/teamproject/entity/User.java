package com.example.teamproject.entity;

import com.example.teamproject.audit.AuditingFields;
import com.example.teamproject.domain.constant.RoleType;
import com.example.teamproject.domain.constant.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
@Getter
@Entity
public class User extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;            // 유저pk

    @Column(name = "login_id", nullable = false)
    private String loginId;     // 로그인 ID

    @Column(name = "password", nullable = false)
    private String password;    // 로그인 비밀번호

    @Column(name = "username", nullable = false)
    private String username;    // 유저이름

    @Column(name = "email", nullable = false)
    private String email;       // 이메일

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;  // 계정 타입

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "USER_CREATED_AT") private LocalDateTime CreatedAt;
    @Column(name = "USER_UPDATED_AT") private LocalDateTime UpdatedAt;



    // id, 생성일자, 수정일자는 자동으로 등록
    @Builder
    private User(Long id, String loginId, String password, String username, String email, RoleType roleType, UserStatus userStatus) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.email = email;
        this.roleType = roleType;
        this.userStatus = userStatus;
    }
}