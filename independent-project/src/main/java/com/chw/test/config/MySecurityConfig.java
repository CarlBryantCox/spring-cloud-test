package com.chw.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String loginPath="/login/oauth2";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义身份验证组件
        auth.inMemoryAuthentication().withUser("wsh").password("$2a$10$PS/yUQ2DdNvh4Fh40vfq7uyzjaXHaTxO7eHCyYo6JzJ1f.jtU9X3K").roles("user");
    }

    @Override
    public void configure(HttpSecurity http) throws  Exception{
        http.authorizeRequests()
                .antMatchers(loginPath).permitAll()
                .antMatchers("/failure").permitAll()
                .antMatchers("/test/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest()
                .authenticated()
                .and().formLogin().loginPage(loginPath).failureForwardUrl("/failure")
        .and().csrf().disable();
        http.oauth2Login().loginPage(loginPath);
    }

}
