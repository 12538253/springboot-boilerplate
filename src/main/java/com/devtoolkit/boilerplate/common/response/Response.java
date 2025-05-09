package com.devtoolkit.boilerplate.common.response;

import com.devtoolkit.boilerplate.common.response.model.ResponseCode;

import java.time.OffsetDateTime;

public sealed interface Response permits Response.Success, Response.Fail {

    record Success(
            String status,
            String code,
            String message,
            Object data,
            String description,
            OffsetDateTime timestamp
    ) implements Response {
        public static Success of(Object data) {
            ResponseCode rc = ResponseCode.SUCCESS;
            return new Success("SUCCESS", rc.code(), rc.message(), data, null, OffsetDateTime.now());
        }
    }

    record Fail(
            String status,
            String code,
            String message,
            Object data,
            String description,
            OffsetDateTime timestamp
    ) implements Response {
        public static Fail of(String code, String message, String description) {
            return new Fail("FAIL", code, message, null, description, OffsetDateTime.now());
        }

        // 선택: enum 기반 Fail 생성도 추가
        public static Fail of(ResponseCode code, String description) {
            return new Fail("FAIL", code.code(), code.message(), null, description, OffsetDateTime.now());
        }
    }
}