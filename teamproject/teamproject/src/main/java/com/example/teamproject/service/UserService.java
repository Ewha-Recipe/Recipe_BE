package com.example.teamproject.service;


import com.example.teamproject.dto.UserDto;
import com.example.teamproject.exception.ProfileApplicationException;
import com.example.teamproject.exception.ErrorCode;
import com.example.teamproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserDto findUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new ProfileApplicationException(ErrorCode.USER_NOT_FOUND));
    }
}
