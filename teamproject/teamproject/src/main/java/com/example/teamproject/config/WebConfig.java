package com.example.teamproject.config;

import com.example.teamproject.interceptor.JwtTokenInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 웹 애플리케이션의 웹 설정을 정의하는 클래스로 인터셉터, 뷰 컨트롤러, 정적 리소스 핸들러 등을 설정
// SpringBoot 애플리케이션의 시작 시점에 로드되어 웹 설정을 초기화
// 사용자가 웹 애플리케이션에 요청을 보낼 때 JwtTokenInterceptor 클래스가 해당 요청을 가로채서 JWT 토큰의 유효성을 검사함.
// 로그인 관련된 URL은 이 검사에서 제외되며, 루트 URL(/)에 접근하면 사용자는 /login 페이지로 redirect 됨.
@Slf4j
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    // 인터셉터 추가, JwtTokenInterceptor를 모든 URL(/**)에 적용하되 로그인 관련된 URL은 제외하도록 설정
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/**") // 모든 URL에 대해 JWT 토큰 검사를 적용합니다.
                .excludePathPatterns("/user/login", "/login", "/main/rootPage"); // 로그인 페이지는 JWT 토큰 검사에서 제외합니다.
    }

    /**
     *  이 배열은 정적 리소스가 위치할 수 있는 경로를 나열하고 있다. 이 경로들은 addResourceHandlers 메서드에서 사용된다.
     */
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/static/",
            "classpath:/public/",
            "classpath:/",
            "classpath:/resources/",
            "classpath:/META-INF/resources/",
            "classpath:/META-INF/resources/webjars/"
    };

    /**
     * 뷰 컨트롤러를 추가할때 사용된다. 여기서는 루트 URL("/")에 접근했을 때 "/login"으로 리다이렉트하도록 설정하고 있다.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        log.debug("[+] WebConfig Start !!! ");
        registry.addRedirectViewController("/", "/login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 정적 리소스를 처리하는 핸들러를 추가하는데 사용된다.
     * 여기서는 모든 요청("/**")에 대해 CLASSPATH_RESOURCE_LOCATIONS에 지정된 경로에서 리소스를 찾도록 설정하고 있다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

}