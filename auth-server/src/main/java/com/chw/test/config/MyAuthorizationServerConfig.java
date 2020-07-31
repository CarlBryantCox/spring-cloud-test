package com.chw.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    /**
     * 配置授权服务器的安全，意味着/oauth/token端点和/oauth/authorize端点都应该是安全的
     * 默认设置覆盖了绝大部分需求，一般情况下不需要做任何事
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
    }

    /**
     * 该处通过配置ClientDetailsService来配置注册到该授权服务器的clients信息
     * 注意：除非下面的configure(AuthorizationServerEndpointsConfigurer endpoints)指定了一个authenticationManager，否则密码授权模式不可用
     * 至少要配制一个client，否则服务器无法启动
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                    //client-id
                    .withClient("client-for-server")
                    //client-secret
                    .secret(passwordEncoder.encode("client-for-server"))
                    //该client支持的授权模式
                    //oauth的client在请求code时，只有传递授权模式参数，该处包含的授权模式才可以访问
                    //可选值：authorization_code、password、refresh_token、implicit和client_credentials
                    .authorizedGrantTypes("authorization_code","implicit")
                    //该client分配的access_token的有效时间要少于刷新时间
                    .accessTokenValiditySeconds(7200)
                    //该client分配的access_token的可刷新时间要多于有效时间
                    //超过有效时间，但在可刷新时间范围内的access_token也可以刷新
                    .refreshTokenValiditySeconds(72000)
                    //重定向URL，这是客户端注册时，客户端提供的
                    .redirectUris("http://localhost:3333/login/oauth2/code/chw-auth")
                    .additionalInformation()
                    //该client可以访问的资源服务器的id
                    .resourceIds(MyResourceServerConfig.RESOURCES_ID)
                    //该client拥有的权限，资源服务器可以依据该处定义的范围对client进行鉴权
                    .authorities("ROLE_CLIENT")
                    //自动批准的范围（scope），自动批准的scope在批准页不需要显示，即不需要用户确认批准
                    //如果所有scope都自动批准，则不显示批准页
                    .scopes("profile","email","phone")
                    .autoApprove("profile");
    }

    /**
     * 该处是用来配置授权服务器特性的，主要是一些非安全的特性，比如token储存、token自定义、授权模式等
     * 默认不需要任何配置，如果需要密码授权，则需要提供一个authenticationManager
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
    }
}
