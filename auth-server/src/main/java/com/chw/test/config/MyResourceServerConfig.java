package com.chw.test.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


/**
 * 资源服务器的职责是对来自oauth客户端的access_token进行鉴权
 * 一个资源服务器包含多个端点(接口)，一部分作为资源服务器的资源提供给oauth的client访问，另一部分不由资源服务器管理
 * 由资源服务器管理的端点安全性配置在此类中，其余端点的安全性配置在SecurityConfiguration类中
 * EnableResourceServer会创建一个WebSecurityConfigurerAdapter，执行顺序（order）是3
 * 在SecurityConfiguration类之前运行，优先级更高
 */
@Configuration
@EnableResourceServer
public class MyResourceServerConfig extends ResourceServerConfigurerAdapter {

    //资源服务器的id
    public static final String RESOURCES_ID = "chw-auth";

    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
        resources.resourceId(RESOURCES_ID);
    }

    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/me")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
    }
}
