/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.listeners;

import com.demo.entities.ActivityEntity;
import com.demo.events.UserActivityEvent;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import com.demo.repositories.ActivityEntityRepository;

/**
 *
 * @author obinna.asuzu
 */
public class UserActivityListener implements ApplicationListener<UserActivityEvent> {
    @Autowired
    ActivityEntityRepository repository;

    @Override
    public void onApplicationEvent(UserActivityEvent e) {
        ActivityEntity log = new ActivityEntity();
        log.setTimestamp(LocalDateTime.now());
        log.setObjectRef(e.getObjectRef());
        log.setObjectDescription(e.getObject());
        log.setVerb(e.getAction());
        log.setSubject(e.getSource());
        repository.save(log);
    }
    
}
