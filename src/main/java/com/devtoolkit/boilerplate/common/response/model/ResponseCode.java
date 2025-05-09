package com.devtoolkit.boilerplate.common.response.model;

/**
 * API 응답 코드 정의
 * - 포맷: [도메인접두][3자리 숫자]
 *   예: S000 (성공), P001 (기획전 오류), A001 (인증 실패)
 */
public enum ResponseCode {
    // System (Sxxx)
    SUCCESS("S000", "성공적으로 처리되었습니다."),
    ERROR("S001", "서버 내부 오류가 발생했습니다."),
    IP_ERROR("S010", "적합한 서버 IP를 찾을 수 없습니다."),

    // Client 오류 (Cxxx)
    INVALID_REQUEST("C001", "잘못된 요청입니다."),
    MISSING_PARAMETER("C002", "필수 파라미터가 누락되었습니다."),
    VALIDATION_ERROR("C003", "입력값이 유효하지 않습니다."),
    NOT_FOUND("C004", "요청한 리소스를 찾을 수 없습니다."),
    TYPE_MISMATCH("C005", "잘못된 파라미터 타입입니다."),

    // 인증/권한 오류 (Axxx)
    UNAUTHORIZED("A001", "인증이 필요합니다."),
    FORBIDDEN("A002", "접근 권한이 없습니다."),
    TOKEN_EXPIRED("A003", "인증 토큰이 만료되었습니다."),
    LOGIN_REQUIRED("A004", "로그인이 필요합니다.");


    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}