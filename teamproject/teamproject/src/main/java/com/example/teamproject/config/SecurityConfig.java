package com.example.teamproject.config;

import com.example.teamproject.filter.CustomAuthenticationFilter;
import com.example.teamproject.filter.JwtAuthorizationFilter;
import com.example.teamproject.security.CustomAuthFailureHandler;
import com.example.teamproject.security.CustomAuthSuccessHandler;
import com.example.teamproject.security.CustomAuthenticationProvider;
import com.example.teamproject.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class SecurityConfig {

    /**
     * 이 메서드는 정적 자원에 대해 보안을 적용하지 않도록 설정한다.
     * 정적 자원은 보통 HTML, CSS, JavaScript, 이미지 파일 등을 의미하며, 이들에 대해 보안을 적용하지 않음으로써 성능을 향상시킬 수 있다.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAuthenticationFilter customAuthenticationFilter,
            JwtAuthorizationFilter jwtAuthorizationFilter
    ) throws Exception {
        log.debug("[+] WebSecurityConfig Start !!! ");
        return http

                // CSRF는 웹 애플리케이션의 취약점 중 하나로, 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위를 하도록 만드는 공격.
                // 이 설정은 CSRF 보호 기능을 비활성화하고, 이렇게 설정하면 CSRF 토큰 없이도 요청을 처리할 수 있게 됨.
                .csrf(AbstractHttpConfigurer::disable)

                // CORS는 다른 도메인의 리소스에 웹 페이지가 접근할 수 있도록 브라우저에게 권한을 부여하는 메커니즘.
                // 아래의 설정은 특정 CORS 구성 소스(corsConfigurationSource())를 사용하여 CORS 설정을 적용.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // HTTP 요청에 대한 인증 및 권한을 정의
                // /resources/**"에 있는 자원에 대한 접근이나 "/main/rootPage" 경로로 들어오는 요청은 모든 사용자에게 허용되도록 설정
                // 그 외의 모든 요청은 인증된 사용자만 접근할 수 있도록 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/resources/**").permitAll()
                        .requestMatchers("/main/rootPage").permitAll()
                        .anyRequest().authenticated()
                )

                // JwtAuthorizationFilter를 BasicAuthenticationFilter전에 실행되도록 작성
                // 이 필터는 요청 헤더에서 JWT 토큰을 추출하고 해당 토큰의 유효성을 검증하는 역할을 함
                // 먼저, JwtAuthorizationFilter가 실행되어 요청 헤더에서 JWT 토큰을 추출하고 이 토큰을 검증함.
                .addFilterBefore(jwtAuthorizationFilter, BasicAuthenticationFilter.class)
                // jwt인증필터(JwtAuthorizationFilter) 또한 SecurityConfig클래스 안에 @Bean으로 등록시킴

                // 세션 관리 전략을 정의.
                // SessionCreationPolicy.STATELESS는 스프링 시큐리티가 세션을 생성하거나 사용하지 않도록 설정되며, 이는 주로 JWT와 같은 토큰 기반 인증에서 사용됨.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // form 기반으로 진행하는 로그인에 관한 설정을 정의
                // 로그인 페이지는 /login으로 설정, 로그인 성공 시 /main/rootPage로 리다이렉트, 모든 사용자가 로그인 페이지에 접근할 수 있도록 허용
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(new SimpleUrlAuthenticationSuccessHandler("/main/rootPage"))
                        .permitAll()
                )

                // CustomAuthenticationFilter는 UsernamePasswordAuthenticationFilter 전에 실행됨
                // 이 사용자 정의 필터는 폼 기반 인증을 처리하며, HTTP 요청에서 사용자 이름과 비밀번호를 추출하여 인증을 시도함
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 이 커스텀 필터 또한 SecurityConfig에서 @Bean으로 등록해줌. -> 클래스가 따로 있지만 초기설정값이 있어서 여기서 빈으로 등록함.

                .build();
    }


    /**
     * 1. 커스텀을 수행한 '인증' 필터로 접근 URL, 데이터 전달방식(form) 등 인증 과정 및 인증 후 처리에 대한 설정을 구성하는 메서드다.
     * 이 메서드는 사용자 정의 인증 필터를 생성한다. 이 필터는 로그인 요청을 처리하고, 인증 성공/실패 핸들러를 설정한다.
     *
     * @return CustomAuthenticationFilter
     */
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(
            AuthenticationManager authenticationManager,
            CustomAuthSuccessHandler customAuthSuccessHandler,
            CustomAuthFailureHandler customAuthFailureHandler
    ) {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager);
        // "/user/login" 엔드포인트로 들어오는 요청을 CustomAuthenticationFilter에서 처리하도록 지정한다.
        customAuthenticationFilter.setFilterProcessesUrl("/user/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthSuccessHandler);    // '인증' 성공 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthFailureHandler);    // '인증' 실패 시 해당 핸들러로 처리를 전가한다.
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    /**
     * 2. authenticate 의 인증 메서드를 제공하는 매니져로'Provider'의 인터페이스를 의미한다.
     * 이 메서드는 인증 매니저를 생성한다. 인증 매니저는 인증 과정을 처리하는 역할을 한다.
     * 과정: CustomAuthenticationFilter → AuthenticationManager(interface) → CustomAuthenticationProvider(implements)
     */
    @Bean
    public AuthenticationManager authenticationManager(CustomAuthenticationProvider customAuthenticationProvider) {
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }

    /**
     * 3. '인증' 제공자로 사용자의 이름과 비밀번호가 요구된다.
     * 이 메서드는 사용자 정의 인증 제공자를 생성한다. 인증 제공자는 사용자 이름과 비밀번호를 사용하여 인증을 수행한다.
     * 과정: CustomAuthenticationFilter → AuthenticationManager(interface) → CustomAuthenticationProvider(implements)
     */
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(UserDetailsService userDetailsService) {
        return new CustomAuthenticationProvider(
                userDetailsService
        );
    }

    /**
     * 4. Spring Security 기반의 사용자의 정보가 맞을 경우 수행이 되며 결과값을 리턴해주는 Handler
     * customLoginSuccessHandler: 이 메서드는 인증 성공 핸들러를 생성한다. 인증 성공 핸들러는 인증 성공시 수행할 작업을 정의한다.
     */
    @Bean
    public CustomAuthSuccessHandler customLoginSuccessHandler() {
        return new CustomAuthSuccessHandler();
    }

    /**
     * 5. Spring Security 기반의 사용자의 정보가 맞지 않을 경우 수행이 되며 결과값을 리턴해주는 Handler
     * customLoginFailureHandler: 이 메서드는 인증 실패 핸들러를 생성한다. 인증 실패 핸들러는 인증 실패시 수행할 작업을 정의한다.
     */
    @Bean
    public CustomAuthFailureHandler customLoginFailureHandler() {
        return new CustomAuthFailureHandler();
    }


    /**
     * "JWT 토큰을 통하여서 사용자를 인증한다." -> 이 메서드는 JWT 인증 필터를 생성한다.
     * JWT 인증 필터는 요청 헤더의 JWT 토큰을 검증하고, 토큰이 유효하면 토큰에서 사용자의 정보와 권한을 추출하여 SecurityContext에 저장한다.
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(CustomUserDetailsService userDetailsService) {
        return new JwtAuthorizationFilter(userDetailsService);
    }

    /**
     * isAdmin 메소드는 Supplier<Authentication>와 RequestAuthorizationContext를 인자로 받아서 "ADMIN" 역할을 가진 사용자인지 확인한다.
     * 만약 사용자가 "ADMIN" 역할을 가지고 있다면, AuthorizationDecision 객체는 true를 반환하고, 그렇지 않다면 false를 반환한다.
     */
    private AuthorizationDecision isAdmin(
            Supplier<Authentication> authenticationSupplier,
            RequestAuthorizationContext requestAuthorizationContext
    ) {
        return new AuthorizationDecision(
                authenticationSupplier.get()
                        .getAuthorities()
                        .contains(new SimpleGrantedAuthority("ADMIN"))
        );
    }

}

