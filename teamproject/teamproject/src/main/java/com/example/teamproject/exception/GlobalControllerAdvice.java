package com.example.teamproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 중앙 집중 예외처리를 위한 GlobalControllerAdvice 선언
 * RestController를 사용중이니 Advice도 @RestControllerAdvice를 사용한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 에러 중앙처리 로직 작성완료
     */
    @ExceptionHandler(ProfileApplicationException.class)
    public ResponseEntity<?> applicationHandler(ProfileApplicationException e) {
        log.error("Error occurs {}", e.toString());

        // 에러 응답을 세팅한다.
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", e.getErrorCode().getCode());
        errorResponse.put("message", e.getErrorCode().getMessage());

        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(errorResponse);
    }


    /**
     * 이 방식을 적용하면 조금 더 상세하게 에러 정보를 볼수가 있다. (NPE같은 특정 예외처리의 세부정보를 제공한다.)
     * 하지만 이 방법은 예외 메시지가 직접 노출되므로 주의해야 한다.
     * 최근에는 이렇게 사용하는게 조금 더 선호되는것 같다.(더 많은 정보를 제공하고, 필터링으로 보안문제를 해결할 수 있고, 유연하다.)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> applicationHandler(RuntimeException e) {
        log.error("Error occurs {}", e.toString());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        errorResponse.put("message", e.getMessage());

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(errorResponse);
    }

}
