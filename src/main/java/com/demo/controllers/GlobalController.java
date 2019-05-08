/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.controllers;

//import com.demo.components.MessageUtil;
import com.demo.dtos.Search;
import com.demo.dtos.Message;
import com.demo.dtos.UserDto;
import com.demo.entities.UserEntity;
import com.demo.exceptions.ServiceException;
import com.demo.repositories.UserEntityRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author obinna.asuzu
 */
@ControllerAdvice
public class GlobalController {

    @Autowired
    UserEntityRepository userEntityRepository;
//    @Autowired
//    private MessageUtil messageUtil;

    @ModelAttribute
    public void global(ModelMap model, Authentication authentication, HttpServletRequest req) throws ServiceException {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserEntity user = (UserEntity) authentication.getPrincipal();
            String[] paths = {
                "/login",
                "/logout",
                "/users/" + username + "/change-password",
                "/users/" + username + "/activate",
                "/error"
            };
            if (!Arrays.asList(paths).contains(req.getRequestURI())) {
                if (user != null && !user.isCredentialsNonExpired()) {
                    System.out.println("Should redirect");
                    ServiceException serviceException = new ServiceException(com.demo.enums.Error.credential_expired);
                    throw serviceException;
                }

            }
        }
    }

}
