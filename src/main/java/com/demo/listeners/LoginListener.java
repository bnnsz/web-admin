/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.listeners;

/**
 *
 * @author obinna.asuzu
 */

import com.demo.entities.ActivityEntity;
import com.demo.enums.ActivityType;
import com.demo.repositories.ActivityEntityRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    
    @Autowired
    ActivityEntityRepository repository;
    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event)
    {
        String username = event.getAuthentication().getName();
        ActivityEntity log = new ActivityEntity();
        log.setTimestamp(LocalDateTime.now());
        log.setVerb(ActivityType.LOGGED_IN);
        log.setSubject(username);
        repository.save(log);
        
    }
}