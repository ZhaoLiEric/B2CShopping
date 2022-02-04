package com.leyou.auth.config;

import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;
    private String priKeyPath;
    private PublicKey publicKey;
    private PrivateKey privateKey;



    private Userprop user = new Userprop();

    @Data
    public class Userprop{
        private Integer expire;//过期时间，单位分钟
        private String cookieName;//LY_TOKEN #cookie名称
        private String cookieDomain;//leyou.com #cookie的域
        private Integer minRefreshInterval;//最小刷新时间间隔
    }

    @PostConstruct//只有pubKeyPath和priKeyPath初始化之后才能获取它们的值
    public void init(){
        //获取公钥
        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
        }catch (Exception e){
            throw new LyException(ExceptionEnum.FILE_READ_ERROR);
        }

    }
}