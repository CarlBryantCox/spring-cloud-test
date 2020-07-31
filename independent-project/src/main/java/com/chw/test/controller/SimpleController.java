package com.chw.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SimpleController {

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @RequestMapping("/")
    public String index(Model model, AbstractAuthenticationToken authenticationToken){
        if(authenticationToken instanceof OAuth2AuthenticationToken){
            OAuth2AuthorizedClient authorizedClient = getAuthorizedClient((OAuth2AuthenticationToken) authenticationToken);
            model.addAttribute("userName",authenticationToken.getName());
            model.addAttribute("clientName",authorizedClient.getClientRegistration().getClientName());
        }else {
            model.addAttribute("clientName","本平台");
        }
        return "index";
    }

    @RequestMapping("/login/oauth2")
    public String login(){
        return "login";
    }

    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authenticationToken){
        return oAuth2AuthorizedClientService.loadAuthorizedClient(authenticationToken.getAuthorizedClientRegistrationId(),authenticationToken.getName());
    }

}
