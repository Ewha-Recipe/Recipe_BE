package com.example.teamproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커스텀한 에러코드를 작성한다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_ALREADY_EXIST(409, "1001", "유저가 이미 존재합니다."),
    USER_NOT_FOUND(404, "1002", "유저를 찾을수 없습니다."),
    INVALID_PASSWORD(401, "1003", "패스워드가 틀렸습니다."),
    DUPLICATED_USER_NAME(409, "1004", "중복된 아이디입니다."),
    INVALID_TOKEN(401, "1005", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(401, "1006", "접근권한이 없습니다."),
    TOKEN_NOT_FOUND(401, "1007", "토큰을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "1008", "서버에 에러가 발생했습니다."),
    BUSINESS_EXCEPTION_ERROR(400, "1009", "비즈니스 로직 에러가 발생했습니다."),
    IO_ERROR(500, "1010", "입출력 에러가 발생했습니다.")
    ;

    private int status;
    private String code;
    private String message;

}
