package com.leyou.common.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

//    用户id
    private Long id;
//    用户名字
    private String username;
//    用户角色
    private String role;
}
