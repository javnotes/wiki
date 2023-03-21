package com.example.wiki.exception;

/**
 * 用于抛出业务异常，比RuntimeException多1个code属性，与前端约定好code，前端根据code做不同的处理
 * 与RuntimeException不同的是，不会写入堆栈信息，提高性能
 * 和ControllerExceptionHandler配合使用
 *
 * @author luf
 * @date 2023/03/21 20:51
 **/
public class BusinessException extends RuntimeException {
    private BusinessExceptionCode code;

    public BusinessException(BusinessExceptionCode code) {
        super(code.getDesc());
        this.code = code;
    }

    public BusinessExceptionCode getCode() {
        return code;
    }

    public void setCode(BusinessExceptionCode code) {
        this.code = code;
    }

    /**
     * 不写入堆栈信息，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}