package com.leyou.common.exceptions;

import lombok.Getter;
import org.joda.time.DateTime;

@Getter
public class ExceptionResult {

    /**
     * {
     *     "timestamp": "2020-02-29T01:32:47.032+0000",
     *     "status": 500,
     *     "error": "Internal Server Error",
     *     "message": "参数错误,没传价格",
     *     "path": "/item"
     * }
     */

    private int status;         //错误状态码
    private String message;     //错误信息
    private String timestamp;   //时间戳


    public ExceptionResult(LyException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
        //通过工具类将当前时间进行赋值
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}
