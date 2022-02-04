package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;
    /**
     * 登录
     * @param userName
     * @param passWord
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam(name = "username")String userName,
                                      @RequestParam(name = "password")String passWord,
                                      HttpServletResponse response){
        authService.login(userName,passWord,response);
        return ResponseEntity.ok().build();
    }

    /**
     * 验证用户
     * @return
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(HttpServletRequest request,HttpServletResponse response){
        return ResponseEntity.ok(authService.verify(request,response));
    }

    /**
     * 退出操作
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response){
        authService.logout(request,response);
        return ResponseEntity.noContent().build();
    }
}
