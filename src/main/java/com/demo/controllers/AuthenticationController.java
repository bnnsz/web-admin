/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.controllers;

import com.demo.services.UserService;
import com.demo.dtos.UserDto;
import com.demo.entities.UserEntity;
import com.demo.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @version 1.0
 * @author obinna.asuzu
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/oauth/v1")
public class AuthenticationController {

    @Autowired
    UserService userService;

   

    @RequestMapping(value = "/request-token", method = RequestMethod.GET)
    public ResponseEntity<String> login(@RequestHeader String authorization) throws ServiceException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = userService.requestToken(auth.getName());
        
        return ResponseEntity.ok(token);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<UserDto> verify(@RequestParam String token) throws Exception {
        UserDetails user = userService.verifyToken(token);
        return ResponseEntity.ok(new UserDto(user));
    }
}
