package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;
    /**
     * 登录
     * @param userName
     * @param passWord
     * @return
     */
    public void login(String userName, String passWord, HttpServletResponse response) {
//        远程调用user服务，获取用户验证信息
        UserDTO userDTO = userClient.queryUser(userName, passWord);
//        构造userInfo,应为userDTO中包含phone，属于敏感信息
        UserInfo userInfo = new UserInfo(userDTO.getId(),userDTO.getUsername(),"admin");
        try{
            //        使用rsa的工具类，获取私钥
            PrivateKey privateKey = prop.getPrivateKey();
//        生成jwt
            String token = JwtUtils.generateTokenExpireInMinutes(userInfo,
                    privateKey,
                    prop.getUser().getExpire());
//        把jwt返回给客户端,放cookie，http请求会自动携带cookie
//        服务端操作cookie
            CookieUtils.newCookieBuilder()
                    .name(prop.getUser().getCookieName())
                    .value(token)
                    .domain(prop.getUser().getCookieDomain())
                    .httpOnly(true)//防止前端使用cookie进行攻击。当前的cookie只能由http请求携带处理。不能由客户端的js操作
                    .response(response)
                    .build();
        }catch(Exception e){
            throw new LyException(500,"登录失败");
        }

    }

    /**
     * 验证用户
     * @return
     */
    public UserInfo verify(HttpServletRequest request,HttpServletResponse response) {
        try{
//        获取用户token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        用jwt工具类解密token,使用公钥来解密token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
//            判断tokenid 是否在黑名单中
            String jti = payload.getId();
            Boolean b = redisTemplate.hasKey(jti);
            if(b != null && b){
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
            //        获取userinfo
            UserInfo userInfo = payload.getUserInfo();
//            获取tokne的过期时间,假设  是12:00
            Date expiration = payload.getExpiration();
//            获取最早刷新时间点。最小刷新间隔是15分钟,计算最早的刷新时间点  12:00 - 15分钟 =11:45
            DateTime refreshTime = new DateTime(expiration).minusMinutes(prop.getUser().getMinRefreshInterval());
//            用当前时间和11:45 进行比较，如果大于11:45，说明token需要续约
            if(refreshTime.isBefore(System.currentTimeMillis())){
//                重新生成token
                token = JwtUtils.generateTokenExpireInMinutes(userInfo, prop.getPrivateKey(), prop.getUser().getExpire());
//                传给客户端，覆盖以前的token
                CookieUtils.newCookieBuilder()
                        .name(prop.getUser().getCookieName())
                        .value(token)
                        .response(response)
                        .domain(prop.getUser().getCookieDomain())
         //防止前端使用cookie进行攻击，当前的cookie只能由http请求携带处理，不能由客户端的js操作
                        .httpOnly(true)
                        .build();
            }

            return userInfo;
        }catch (Exception e){
            e.printStackTrace();
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 退出操作
     * @param request
     * @param response
     * @return
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try{
//        获取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        解密token，获取payload
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
//        拿到tokenid
            String jti = payload.getId();
//        获取有效期
            Date expiration = payload.getExpiration();
//        计算黑名单的有效期
            long time = expiration.getTime()-System.currentTimeMillis();
//        放入redis的黑名单,设置有效期=剩余有效期
            if(time>0){
                redisTemplate.opsForValue().set(jti,"1",time, TimeUnit.MILLISECONDS);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
//        删除cookie
            CookieUtils.deleteCookie(prop.getUser().getCookieName()
                    ,prop.getUser().getCookieDomain()
                    ,response);
        }


    }
}
