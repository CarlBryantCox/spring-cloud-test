package com.chw.test.security;

import com.alibaba.fastjson.JSONObject;
import com.chw.test.dto.ApiResponseDTO;
import com.chw.test.enums.ResponseCodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户未登录，返回自定义提示
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Value("${rest:true}")
    private Boolean rest;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if(rest){
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSONObject.toJSONString(new ApiResponseDTO(ResponseCodeEnum.Not_Login.getCode(),ResponseCodeEnum.Not_Login.getMsg(),"failure")));
        }else {
            /*
             * oauth2，第三方登录跳转自定义登录页面，要把queryString带上
             * 自定义的登录页面，在发起登录请求时，也要带上queryString，以便在登录成功后，进行重定向
             */
            String url = "http://localhost:3326/page/login";
            String queryString = request.getQueryString();
            if(!StringUtils.isEmpty(queryString)){
                url=url+"?"+queryString;
            }
            response.sendRedirect(url);
        }
    }
}

