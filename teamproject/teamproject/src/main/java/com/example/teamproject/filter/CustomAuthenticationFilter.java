package com.example.teamproject.filter;


import com.example.teamproject.dto.UserDto;
import com.example.teamproject.exception.ErrorCode;
import com.example.teamproject.exception.ProfileApplicationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Spring Security의 UsernamePasswordAuthenticationFilter 클래스를 상속받아 사용자 정의 인증 필터를 구현한 것
// 사용자의 로그인 요청을 처리하고, 인증을 시도
// 사용자가 제출한 사용자 이름과 비밀번호를 기반으로 인증 토큰을 생성하고, 이를 AuthenticationManager에 전달하여 인증을 시도
// 인증이 성공하면 인증된 사용자의 정보와 권한을 담은 Authentication 객체를 생성하여 SecurityContext에 저장
// 이 필터는 /user/login 엔드포인트로 들어오는 요청을 처리

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

   // AuthenticationManager 객체를 인자로 받아 부모 클래스의 생성자에 전달
   // AuthenticationManager는 인증을 처리하는 데 사용됨
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * 이 메서드는 사용자가 로그인을 시도할 때 호출된다.
     * HTTP 요청에서 사용자 이름과 비밀번호를 추출하여 UsernamePasswordAuthenticationToken 객체를 생성하고, 이를 AuthenticationManager에 전달하여 인증을 시도한다.
     * 인증이 성공하면 인증된 사용자의 정보와 권한을 담은 Authentication 객체를 반환하고, 인증이 실패하면 AuthenticationException을 던진다.
     */
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        UsernamePasswordAuthenticationToken authRequest;

        try {
            authRequest = getAuthRequest(request);
            setDetails(request, authRequest);
        } catch (Exception e) {
            throw new ProfileApplicationException(ErrorCode.BUSINESS_EXCEPTION_ERROR);
        }

        // Authentication 객체를 반환한다.
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * 이 메서드는 HTTP 요청에서 사용자 이름과 비밀번호를 추출하여 UsernamePasswordAuthenticationToken 객체를 생성하는 역할을 한다.
     * HTTP 요청의 입력 스트림에서 JSON 형태의 사용자 이름과 비밀번호를 읽어 UserDto 객체를 생성하고, 이를 기반으로 UsernamePasswordAuthenticationToken 객체를 생성한다.
     */
    // 스프링 시큐리티의 핵심 인증 메커니즘에서 중요한 역할
    // 사용자가 로그인을 시도할 때, 이 토큰은 제출된 아이디와 비밀번호로 생성됨
    // 이 토큰은 인증 과정 중에만 사용되고 인증이 완료되면 SecurityContext에 저장되어
    // 애플리케이션 전반에서 현재 인증된 사용자의 정보를 쉽게 참조할 수 있게 해줌.
    private UsernamePasswordAuthenticationToken getAuthRequest(
            HttpServletRequest request
    ) throws Exception {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

            UserDto user = objectMapper.readValue(request.getInputStream(), UserDto.class);
            log.debug("1.CustomAuthenticationFilter :: loginId: " + user.loginId() + "userPw: " + user.password());

            /**
             * ID, PW를 기반으로 UsernamePasswordAuthenticationToken 토큰을 발급한다.
             * UsernamePasswordAuthenticationToken 객체가 처음 생성될 때 authenticated 필드는 기본적으로 false로 설정된다.
             */
            return new UsernamePasswordAuthenticationToken(user.loginId(), user.password());
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new ProfileApplicationException(ErrorCode.IO_ERROR);
        }

    }


}
