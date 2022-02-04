package com.leyou;


import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

//@SpringBootApplication
//@EnableEurekaClient      //官方推荐如果注册中心是eureka，尽量用
//@EnableDiscoveryClient   //注册中心可以采用zookeeper
//@EnableCircuitBreaker

@SpringCloudApplication
@EnableZuulProxy   //开启zuul的网关功能
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LyGateway {

    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class,args);
    }
}
