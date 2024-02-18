package com.example.teamproject.filter;

//  Spring Security에서 사용되는 JWT 인증 필터를 정의
//  클라이언트의 요청이 서버에 도달하기 전에 실행되어 JWT 토큰의 유효성을 검사하고, 해당 토큰에 기반한 사용자 인증을 수행
// JwtAuthorizationFilter 코드는 OncePerRequestFilter를 상속받아 요청당 한 번만 이 필터가 실행되도록 함

import com.example.teamproject.exception.ErrorCode;
import com.example.teamproject.exception.ProfileApplicationException;
import com.example.teamproject.util.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 지정한 URL별 JWT의 유효성 검증을 수행하며 직접적인 사용자 인증을 확인합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // userDetailsService는 Spring Security의 UserDetailsService 인터페이스를 구현한 서비스를 주입받음
    // 이 서비스 코드는 사용자의 세부 정보를 로드하는 데 사용됨
    private final UserDetailsService userDetailsService;

    @Override
    // 필터의 주요 로직을 포함하며, 요청이 들어올 때마다 실행됨
    // 토큰이 필요하지 않은 API URL을 정의하고 해당 URL에 대한 요청이 들어오면 다음 필터로 이동. OPTIONS 요청의 경우에도 다음 필터로 이동함.
    // 클라이언트의 쿠키에서 "jwt"라는 이름의 쿠키를 찾아 JWT 토큰을 가져옴
    // 만약 토큰이 유효한 경우, 토큰에서 사용자 ID를 추출하고 해당 ID를 사용하여 사용자의 세부 정보를 로드함.
    // 그 후 인증 토큰을 생성하고 SecurityContext에 설정함.
    // 토큰이 유효하지 않거나 존재하지 않는 경우, 예외를 발생시키고 클라이언트에 JSON 형식의 오류 응답을 반환함.
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 토큰이 필요하지 않은 API URL에 대해서 배열로 구성한다.
        List<String> list = Arrays.asList(
                "/user/login",  // 로그인 페이지의 URL을 추가합니다.
                "/login",  // 로그인 페이지의 URL을 추가합니다.
                "/css/**",
                "/js/**",
                "/images/**"
        );

        // 2. 토큰이 필요하지 않은 API URL의 경우 -> 로직 처리없이 다음 필터로 이동한다.
        if (list.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. OPTIONS 요청일 경우 -> 로직 처리 없이 다음 필터로 이동
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        // [STEP.1] Client에서 API를 요청할때 쿠키를 확인한다.
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        try {
            // [STEP.2-1] 쿠키 내에 토큰이 존재하는 경우
            if (token != null && !token.equalsIgnoreCase("")) {

                // [STEP.2-2] 쿠키 내에있는 토큰이 유효한지 여부를 체크한다.
                if (TokenUtils.isValidToken(token)) {

                    // [STEP.2-3] 추출한 토큰을 기반으로 사용자 아이디를 반환받는다.
                    String loginId = TokenUtils.getUserIdFromToken(token);
                    log.debug("[+] loginId Check: " + loginId);

                    // [STEP.2-4] 사용자 아이디가 존재하는지에 대한 여부를 체크한다.
                    if (loginId != null && !loginId.equalsIgnoreCase("")) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                    } else {
                        throw new ProfileApplicationException(ErrorCode.USER_NOT_FOUND);
                    }
                }
                // [STEP.2-5] 토큰이 유효하지 않은 경우
                else {
                    throw new ProfileApplicationException(ErrorCode.INVALID_TOKEN);
                }
            }
            // [STEP.3] 토큰이 존재하지 않는 경우
            else {
                throw new ProfileApplicationException(ErrorCode.TOKEN_NOT_FOUND);
            }
        } catch (Exception e) {
            // 로그 메시지 생성
            String logMessage = (String) jsonResponseWrapper(e).get("message");
            log.error(logMessage, e);  // 로그에만 해당 메시지를 출력합니다.

            // 클라이언트에게 전송할 고정된 메시지
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            PrintWriter printWriter = response.getWriter();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", true);
            jsonObject.put("message", "로그인 에러");

            printWriter.print(jsonObject);
            printWriter.flush();
            printWriter.close();
        }
    }


    /**
     * 토큰 관련 Exception 발생 시 예외 응답값 구성
     */
    // 발생한 예외에 따라 적절한 오류 메시지를 설정하고, 이를 JSON 객체로 변환하여 반환함.
    // JWT 토큰의 만료, 서명 오류, 파싱 오류 등 다양한 예외 유형을 처리함.
    private JSONObject jsonResponseWrapper(Exception e) {

        String resultMessage = "";
        // JWT 토큰 만료
        if (e instanceof ExpiredJwtException) {
            resultMessage = "TOKEN Expired";
        }
        // JWT 허용된 토큰이 아님
        else if (e instanceof SignatureException) {
            resultMessage = "TOKEN SignatureException Login";
        }
        // JWT 토큰내에서 오류 발생 시
        else if (e instanceof JwtException) {
            resultMessage = "TOKEN Parsing JwtException";
        }
        // 이외 JTW 토큰내에서 오류 발생
        else {
            resultMessage = "OTHER TOKEN ERROR";
        }

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("status", 401);
        jsonMap.put("code", "9999");
        jsonMap.put("message", resultMessage);
        jsonMap.put("reason", e.getMessage());
        JSONObject jsonObject = new JSONObject(jsonMap);
        log.error(resultMessage, e);
        return jsonObject;
    }

}
