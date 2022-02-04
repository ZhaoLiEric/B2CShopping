package com.leyou.gateway.filter;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Slf4j
@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;
    @Autowired
    private FilterProperties filterProperties;
    /**
     * 指定过滤器的类型
     * 前置类型
     * @return
     */
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 执行顺序
     * 数字越小优先级越高
     * 指定一个过滤器的顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }

    /**
     * 是否执行当前过滤器业务
     * true - 执行过滤器  false-不执行
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return !isAllowPath();
    }

    private boolean isAllowPath(){
//        获取白名单列表
        List<String> allowPaths = filterProperties.getAllowPaths();
//        获取当前请求路径
        //        获取请求的上下文
        RequestContext ctx = RequestContext.getCurrentContext();
//        获取当前的请求对象
        HttpServletRequest request = ctx.getRequest();
        // 获取当前资源路径
        String path = request.getRequestURI();
        for (String allowPath : allowPaths) {
            if(path.startsWith(allowPath)){
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤器业务
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
//        获取请求的上下文
        RequestContext ctx = RequestContext.getCurrentContext();
//        获取当前的请求对象
        HttpServletRequest request = ctx.getRequest();
        try{
//        获取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        使用公钥解密token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
//        获取用户信息
            UserInfo userInfo = payload.getUserInfo();
//        补充：判断用户权限
            String role = userInfo.getRole();
            // 获取当前资源路径
            String path = request.getRequestURI();
            String method = request.getMethod();
            // TODO 判断权限，此处暂时空置，等待权限服务完成后补充
            log.info("【网关】用户{},角色{}。访问服务{} : {}，", userInfo.getUsername(), role, method, path);
        }catch(Exception e){
            e.printStackTrace();
            //        如果出现错误，让用户重新登录
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
        }
        return null;
    }
}
