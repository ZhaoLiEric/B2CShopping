package com.leyou.common.test;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class TestRsa {

    /**
     * 生成公钥和私钥
     * 读取公钥私钥文件生成java对象
     *
     */
    @Test
    public void testRsa() throws Exception {

//        公钥文件名
        String pubKeyPath = "F:\\itcast-work\\heima-jee121\\ssh\\id_rsa.pub";
//        私钥文件名
        String privateKeyPath = "F:\\itcast-work\\heima-jee121\\ssh\\id_rsa";

        RsaUtils.generateKey(pubKeyPath,
                privateKeyPath,
                "helloworld",
                1024);
//      获取公钥对象
        PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);
//        获取私钥对象
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        System.out.println(publicKey);
        System.out.println("-------------------------------");
        System.out.println(privateKey);

    }

    /**
     * 测试jwt的工具类
     */
    @Test
    public void testJwt() throws Exception {
//        公钥文件名
        String pubKeyPath = "F:\\itcast-work\\heima-jee121\\ssh\\id_rsa.pub";
//        私钥文件名
        String privateKeyPath = "F:\\itcast-work\\heima-jee121\\ssh\\id_rsa";
//        构造用户对象
        UserInfo userInfo = new UserInfo(234L,"jack","admin");
//        私钥对象
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
//        生成jwt
//        String jwt = JwtUtils.generateTokenExpireInSeconds(userInfo, privateKey, 300);

//        System.out.println(jwt);

        String jwt1 = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoyMzQsXCJuYW1lXCI6XCJqYWNrXCIsXCJyb2xlXCI6XCJhZG1pblwifSIsImp0aSI6Ik1tRXlaVGs0Tm1VdE5XTTBNaTAwWkROakxXSTNZell0WXpRNFpqZ3hZbUZoTlRreiIsImV4cCI6MTU4NDM0MjEwNX0.ez05TMXOE8GPXSXJMDxoI77Eg09OP2gCkgGAPH8CT8gzEtTGmYhlWiryflukNPwKsDBoTH9PgTPhXdoMDZi2YwCIp-c5HjrspOwd-yf78bgt7m54IlCvwdb5u_e9shi7A2wucOCOoaDkPeV5S9B97A0Xi1S41IJuIgvf-GkjOn-AYBRv7UmMwSCNEkmgJ7jiN3Te7VPBvjuHpEfzh4ZsVL5FOZszJ2nbarEjCHxE-RszXuc0QoJd2L72u13YQYK6hFhIrw7vrnPWtRftP20ZJ0_sYeOXKQcTlabvVQYNVv68gyiunAUWkv__tQp_LhQbDSyMK8AJ1upOLEWhuN2OyA";

        PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);
//        解析jwt，使用公钥
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(jwt1, publicKey, UserInfo.class);
        System.out.println("jwt的id="+payload.getId());
        System.out.println("过期时间="+payload.getExpiration());
        UserInfo userInfo1 = payload.getUserInfo();
        System.out.println("用户名:"+userInfo1.getUsername());
        System.out.println("用户id:"+userInfo1.getId());
        System.out.println("用户角色:"+userInfo1.getRole());

    }
}
