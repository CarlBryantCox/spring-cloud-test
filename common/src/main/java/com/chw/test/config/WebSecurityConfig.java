package com.chw.test.config;

import com.chw.test.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 开始debug日志打印 @EnableWebSecurity(debug=true)
 */

//开启方法注解鉴权
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //key 是标记 是从哪里获取的token 可以随便定义
    private static final String key="chw-security";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public RememberMeServices rememberMeServices(){
        //spring security提供的持久化令牌方案
        //可以实现PersistentTokenRepository接口定制自己的持久化令牌方案
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return new MyRememberMeServices(key,userDetailsService,jdbcTokenRepository);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration= new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST"));
        corsConfiguration.setAllowedOrigins(Arrays.asList("localhost","127.0.0.1"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }

    @Bean
    public MyAuthenticationEntryPoint myAuthenticationEntryPoint(){
        return new MyAuthenticationEntryPoint();
    }

    @Bean
    public MyLoginSuccessHandler myLoginSuccessHandler(){
        return new MyLoginSuccessHandler();
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义身份验证组件
        auth.authenticationProvider(new MyAuthenticationProvider(userDetailsService));
    }


    //这个配置是基于session的
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        MyLoginFilter myLoginFilter = new MyLoginFilter();
        myLoginFilter.setAuthenticationSuccessHandler(myLoginSuccessHandler());
        myLoginFilter.setAuthenticationFailureHandler(new MyLoginFailureHandler());
        myLoginFilter.setAuthenticationManager(authenticationManager);
        myLoginFilter.setRememberMeServices(rememberMeServices());//使用自定义的记住我实现

        //使用自定义的登陆filter
        http.addFilterBefore(myLoginFilter,UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .antMatchers("/token").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers("/page/login").permitAll()
                //feign调用放行
                .antMatchers("/api/feign/**").permitAll()
                .antMatchers("/druid/**").permitAll()
                //其余任何请求都需要登录
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                //关闭csrf
                .csrf().disable()
                //开启记住我
                .rememberMe()
                .key(key)
                .rememberMeServices(rememberMeServices())
                .and()
                //退出登录 spring security默认注册了 /logout 路由
                .logout()
                .logoutSuccessHandler(new MyLogoutSuccessHandler())
                .and()
                .cors()
                .and()
                //开启共享session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .maximumSessions(1);

        //没有登录和权限使用自定义的返回结果
        http.exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler());

        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager,new MyLoginFailureHandler()), UsernamePasswordAuthenticationFilter.class);
    }

    /*
    //也可以不使用session，使用jwt
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/error").permitAll()
                .antMatchers("/token").permitAll()
                .antMatchers("/loginPage").permitAll()
                //feign调用放行
                .antMatchers("/api/feign/**").permitAll()
                //其余任何请求都需要登录
                .anyRequest().authenticated()
                .and()
                //关闭csrf
                .csrf().disable()
                .cors()
                .and()
                //不需要session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //没有登录和权限使用自定义的返回结果
        http.exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler());

        // 将自定义的token验证过滤器添加到UsernamePasswordAuthenticationFilter过滤器之前
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager,new MyLoginFailureHandler()), UsernamePasswordAuthenticationFilter.class);
    }
    */
}
