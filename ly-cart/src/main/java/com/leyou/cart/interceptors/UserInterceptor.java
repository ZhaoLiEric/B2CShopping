package com.leyou.cart.interceptors;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    /**
     * 拦截器刚拦截到请求
     * 获取token，获取payload ，获取uid ，放入threadlocal
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
            Long userId = payload.getUserInfo().getId();
            UserHolder.setUser(userId);
            log.info("获取到用户id，{}",userId);
            return true;
        }catch(Exception e){
            log.error("【购物车服务】解析用户信息失败！", e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除threadlocal中的内容
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("删除threadlocal的userId");
        UserHolder.removeUser();
    }
}
