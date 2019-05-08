/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.components;

import com.demo.enums.ActivityType;
import com.demo.events.UserActivityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author obinna.asuzu
 */
public class ActivityLogger {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    
    public void log(ActivityType type, String object, String objectRef) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            UserActivityEvent event = new UserActivityEvent(type, username, object, objectRef);
            applicationEventPublisher.publishEvent(event);
        }
    }
}
