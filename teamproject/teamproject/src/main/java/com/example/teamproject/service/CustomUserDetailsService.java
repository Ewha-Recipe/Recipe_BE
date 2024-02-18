package com.example.teamproject.service;

import com.example.teamproject.dto.SecurityUserDetailsDto;
import com.example.teamproject.dto.UserDto;
import com.example.teamproject.exception.ErrorCode;
import com.example.teamproject.exception.ProfileApplicationException;
import com.example.teamproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.teamproject.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // 1. userRepository로부터 loginId로 유저정보를 받아온다.
        User byLoginId = userRepository.findByLoginId(loginId)
                .orElseThrow(
                        () -> new ProfileApplicationException(ErrorCode.USER_NOT_FOUND)
                );

        // 2.user를 dto로 변환시켜준다.
        UserDto userDto = UserDto.fromEntity(byLoginId);

        // 3. 사용자 정보를 기반으로 SecurityUserDetailsDto 객체를 생성한다.
        return new SecurityUserDetailsDto(
                userDto,
                Collections.singleton(new SimpleGrantedAuthority(
                        userDto.roleType().toString()
                ))
        );
    }

}
