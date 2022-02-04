package com.leyou.common.auth.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Payload<T> {
//    jwtid
    private String id;
//    用户信息
    private T userInfo;
//    过期时间
    private Date expiration;
}
