package com.chw.test.security;

import com.alibaba.fastjson.JSONObject;
import com.chw.test.dto.ApiResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义登录成功后的响应
 */
@Component
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler{

    @Value("${rest:true}")
    private Boolean rest;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(rest){
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSONObject.toJSONString(new ApiResponseDTO()));
        }else {
            /*
             * oauth2的登录请求，成功后，进行重定向
             */
            String queryString = request.getQueryString();
            String url = "http://localhost:3326/hello";
            if(!StringUtils.isEmpty(queryString)){
                url="http://localhost:3326/oauth/authorize?"+queryString;
            }
            System.out.println("url="+url);
            response.sendRedirect(url);
        }
    }
}
