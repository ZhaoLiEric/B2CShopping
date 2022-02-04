package com.leyou.common.exceptions;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;

/**
 * 自定义异常类，继承RuntimeException
 */
@Getter
public class LyException extends RuntimeException {

    /**
     * 错误信息的状态码
     * @param en
     */
    private int status;


    public LyException(ExceptionEnum em) {
        super(em.getMessage());
        this.status = em.getStatus();
    }

    public LyException(int status, String msg) {
        super((msg));
        this.status = status;
    }
}
