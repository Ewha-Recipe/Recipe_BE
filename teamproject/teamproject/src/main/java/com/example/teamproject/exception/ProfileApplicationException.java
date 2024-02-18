package com.example.teamproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;



/**
 * User 커스텀 예외처리 클래스
 */
@Getter
@AllArgsConstructor
public class ProfileApplicationException extends RuntimeException {

    private ErrorCode errorCode;

}