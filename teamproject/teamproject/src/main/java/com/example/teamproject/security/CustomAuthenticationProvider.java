package com.example.teamproject.security;

import com.example.teamproject.dto.SecurityUserDetailsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 스프링 시큐리티의 AuthenticationProvider 인터페이스를 구현한 사용자 정의 인증 제공자.
// 사용자의 인증을 처리하는 역할
// 사용자 이름과 비밀번호를 기반으로 인증 과정을 수행
// CustomAuthenticationFilter, AuthenticationManager를 거쳐 마지막으로 CustomAuthenticationProvider에서 처리됨

// 사용자가 로그인을 시도할 때, CustomAuthenticationFilter 클래스의 attemptAuthentication 메서드에서 생성된
// UsernamePasswordAuthenticationToken 객체가 AuthenticationManager에 전달됨
// 전달된 토큰을 받은 AuthenticationManager는 등록된 AuthenticationProvider(인증 제공자)들 중에서 해당 토큰을 처리할 수 있는 제공자를 찾으며
// 이때 supports 메서드가 사용됨
// 만약 CustomAuthenticationProvider가 해당 토큰을 지원한다면 (supports 메서드가 true를 반환한다면),
// CustomAuthenticationProvider 클래스 내부의 authenticate 메서드가 호출되어 사용자의 인증을 처리하게 됨.
// 즉 이 클래스는 사용자의 인증을 처리하는 사용자 정의 인증 제공자로, 사용자가 로그인을 시도할 때
// 해당 제공자의 authenticate 메서드가 호출되어 인증 과정이 수행됨.

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;


    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    // Authentication 객체를 매개변수로 받아 사용자 이름과 비밀번호를 추출
    // 내가 재정의한 CustomUserDetailsService를 사용해 데이터베이스에서 사용자 정보를 로드
    // 로드된 사용자 정보는 Bcrypt로 암호화된 비밀번호와 사용자가 입력한 폼의 비밀번호를 비교하는 데 사용됨
    // 인증 과정이 성공하면, 인증된 사용자 정보와 권한이 포함된 UsernamePasswordAuthenticationToken 객체를 반환
    // 인증이 실패하면 BadCredentialsException이 발생
    // 시스템의 보안성을 높이는 데 중요한 역할이자 사용자 인증에 있어서 핵심적인 부분
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("2.CustomAuthenticationProvider");

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // AuthenticationFilter에서 생성된 토큰으로부터 ID, PW를 조회
        String loginId = token.getName();
        String userPassword = (String) token.getCredentials();

        // Spring security - UserDetailsService를 통해 DB에서 username으로 사용자 조회
        SecurityUserDetailsDto securityUserDetailsDto = (SecurityUserDetailsDto) userDetailsService.loadUserByUsername(loginId);

        // 대소문자를 구분하는 matches() 메서드로 db와 사용자가 제출한 비밀번호를 비교
        if (!bCryptPasswordEncoder().matches(userPassword, securityUserDetailsDto.getUserDto().password())) {
            throw new BadCredentialsException(securityUserDetailsDto.getUsername() + "Invalid password");
        }

        // 인증이 성공하면 인증된 사용자의 정보와 권한을 담은 새로운 UsernamePasswordAuthenticationToken을 반환한다.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(securityUserDetailsDto, userPassword, securityUserDetailsDto.getAuthorities());
        return authToken;
    }

    @Override
    // AuthenticationProvider가 특정 Authentication 타입을 지원하는지 여부를 반환
    // UsernamePasswordAuthenticationToken 클래스를 지원한다고 명시
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
