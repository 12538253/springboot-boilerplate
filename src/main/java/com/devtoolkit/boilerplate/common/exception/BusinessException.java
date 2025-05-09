package com.devtoolkit.boilerplate.common.exception;

import com.devtoolkit.boilerplate.common.response.model.ResponseCode;

public class BusinessException extends RuntimeException {

    private final ResponseCode code;
    private final String description;

    public BusinessException(ResponseCode code) {
        super(code.message());
        this.code = code;
        this.description = null;
    }

    public BusinessException(ResponseCode code, String description) {
        super(code.message());
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code.code();
    }

    public ResponseCode getCodeEnum() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
