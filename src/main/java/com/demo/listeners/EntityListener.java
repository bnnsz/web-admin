/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.listeners;

import com.demo.components.AutowireHelper;
import com.demo.entities.AbstractEntity;
import com.demo.entities.ActivityEntity;
import com.demo.enums.ActivityType;
import static com.demo.enums.ActivityType.*;
import java.time.LocalDateTime;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import com.demo.repositories.ActivityEntityRepository;

/**
 *
 * @author ObinnaAsuzu
 */
public class EntityListener {

    @Autowired
    ActivityEntityRepository repository;
    

    

    @PrePersist
    public void prePersist(AbstractEntity item) {
        item.setCreatedTimestamp(LocalDateTime.now());
        item.setUpdatedTimestamp(LocalDateTime.now());
        audit(item, CREATED);
    }

    @PreUpdate
    public void preUpdate(AbstractEntity item) {
        item.setUpdatedTimestamp(LocalDateTime.now());
        audit(item, UPDATED);
    }

    @PreRemove
    public void preRemove(AbstractEntity item) {
        audit(item, REMOVED);
    }

    public void audit(AbstractEntity item, ActivityType action) {
        AutowireHelper.autowire(this, this.repository);
        ActivityEntity log = new ActivityEntity();
        log.setTimestamp(LocalDateTime.now());
        log.setObjectRef(String.valueOf(item.getId()));
        log.setObjectDescription(item.getDescription());
        log.setVerb(action);
        try {
            log.setSubject(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
            log.setSubject("Application");
        }
        repository.save(log);
    }
}
