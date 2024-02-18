package com.example.teamproject.security;

import com.example.teamproject.dto.SecurityUserDetailsDto;
import com.example.teamproject.dto.UserDto;
import com.example.teamproject.util.TokenUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// 스프링 시큐리티의 SavedRequestAwareAuthenticationSuccessHandler를 상속받아 사용자 정의 인증 성공 핸들러를 구현
// 이 핸들러는 사용자가 인증에 성공했을 때 수행되는 로직을 정의

// 사용자 정보 조회: Authentication 객체에서 사용자 정보를 가져온다.
// 데이터 파싱: 조회한 사용자 데이터를 JSONObject 형태로 변환한다.
// 응답 데이터 구성: 사용자의 상태에 따라 응답 데이터를 다르게 구성한다.
    // 휴면 상태: 사용자의 상태가 '휴먼 상태'인 경우, 해당 정보와 함께 특정 응답 코드와 메시지를 응답 데이터에 포함시킨다.
    //  일반 상태: 사용자의 상태가 '휴먼 상태'가 아닌 경우, 사용자 정보와 함께 정상 응답 코드를 응답 데이터에 포함시킨다. 또한, JWT 토큰을 생성하고 이를 응답 데이터와 쿠키에 포함시킨다.
//응답 전송: 구성한 응답 데이터를 클라이언트에 전송한다.

// JWT 토큰 생성: TokenUtils.generateJwtToken(userDto)를 통해 사용자 정보를 기반으로 JWT 토큰을 생성한다.
//쿠키 저장: 생성된 JWT 토큰을 jwt라는 이름의 쿠키에 저장하고, 이 쿠키를 응답에 포함시킨다.
//          이렇게 함으로써 클라이언트는 이후의 요청에서 이 쿠키를 포함하여 서버에 전송할 수 있다.

// 즉 CustomAuthSuccessHandler는 사용자가 인증에 성공했을 때 수행되는 로직을 정의하는 핸들러
// 사용자의 상태에 따라 다른 응답을 구성하고, 일반 상태의 사용자에게는 JWT 토큰을 발급하여 응답과 쿠키에 포함시킴
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    // 사용자가 인증에 성공했을 때 호출됨
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.debug("3.CustomLoginSuccessHandler");

        // 1. 사용자와 관련된 정보를 모두 조회한다.
        UserDto userDto = ((SecurityUserDetailsDto) authentication.getPrincipal()).getUserDto();

        // 2. 조회한 데이터를 JSONObject 형태로 파싱한다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Map<String, Object> userDtoMap = objectMapper.convertValue(userDto, new TypeReference<Map<String, Object>>() {});
        JSONObject userDtoObject = new JSONObject(userDtoMap);

        HashMap<String, Object> responseMap = new HashMap<>();
        JSONObject jsonObject;

        // 3-1. 사용자의 상태가 '휴먼 상태' 인 경우에 응답값으로 전달할 데이터
        if (Objects.equals(userDto.status(), "D")) {
            responseMap.put("userInfo", userDtoObject);
            responseMap.put("resultCode", 9001);
            responseMap.put("token", null);
            responseMap.put("failMessage", "휴면 계정입니다.");
            jsonObject = new JSONObject(responseMap);
        }
        // 3-2. 사용자의 상태가 '휴먼 상태'가 아닌 경우에 응답값으로 전달할 데이터
        else {
            // 1. 일반 계정일 경우 데이터 세팅
            responseMap.put("userInfo", userDtoObject);
            responseMap.put("resultCode", 200);
            responseMap.put("failMessage", null);
            jsonObject = new JSONObject(responseMap);

            // JWT 토큰 생성
            String token = TokenUtils.generateJwtToken(userDto);
            jsonObject.put("token", token);

            // 쿠키에 JWT 토큰 저장
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true); // JavaScript에서 쿠키에 접근할 수 없도록 설정
            jwtCookie.setPath("/"); // 모든 경로에서 쿠키에 접근 가능하도록 설정
            response.addCookie(jwtCookie); // 응답에 쿠키 추가
        }


        // 4. 구성한 응답값을 전달한다.
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try (PrintWriter printWriter = response.getWriter()){
            printWriter.print(jsonObject); // 최종 저장된 '사용자 정보', '사이트 정보'를 Front에 전달
            printWriter.flush();
        }
    }

}
