/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.controllers;

import com.demo.dtos.UserDto;
import com.demo.entities.UserEntity;
import com.demo.exceptions.ServiceException;
import com.demo.forms.ChangePasswordForm;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author obinna.asuzu
 */
/**
 *
 * @author obinna.asuzu
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest req, Exception ex) {
        ex.printStackTrace();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", "500");
        modelAndView.addObject("phrase", "Internal server Error");
        modelAndView.addObject("message", ex.getMessage());
        // add other objects to the model here
        modelAndView.setViewName("error.html");

        return modelAndView;
    }

    @ExceptionHandler(ServiceException.class)
    public ModelAndView handleError(HttpServletRequest req, Authentication authentication, ServiceException ex) {
        ex.printStackTrace();
        ModelAndView modelAndView = new ModelAndView();
        if (ex.getError() == com.demo.enums.Error.credential_expired) {
            String username = authentication.getName();
            UserEntity user = (UserEntity) authentication.getPrincipal();

            String password = user.getPassword();
            password = password.trim().isEmpty() ? null : password;

            modelAndView.addObject("form", new ChangePasswordForm(password));
            modelAndView.addObject("username", username);
            modelAndView.setViewName("changepassword.html");
        } else {
            modelAndView.addObject("code", ex.getError().getStatus().value());
            modelAndView.addObject("phrase", ex.getError().getStatus().getReasonPhrase());
            modelAndView.addObject("message", ex.getError().getMessage());
            // add other objects to the model here
            modelAndView.setViewName("error.html");
        }
        return modelAndView;
    }
}
