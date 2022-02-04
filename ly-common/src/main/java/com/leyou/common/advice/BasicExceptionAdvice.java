package com.leyou.common.advice;

import com.leyou.common.exceptions.ExceptionResult;
import com.leyou.common.exceptions.LyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 异常拦截增强类
 */
@ControllerAdvice
public class BasicExceptionAdvice {


    /**
     *  通过ExceptionHandler 捕获异常，参数是要捕获异常的类
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e){
         return ResponseEntity.status(HttpStatus.CREATED).body(e.getMessage());
    }


    /**
     * 抛自定义异常的时候，将默认的错误信息，自定义处理
     * @return 自定义错误信息的返回
     */
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleLyException(LyException e){
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }

}
