/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.events;

import com.demo.enums.ActivityType;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author obinna.asuzu
 */
public class UserActivityEvent extends ApplicationEvent {

    private final ActivityType action;
    private final String object;
    private final String objectRef;

    public UserActivityEvent(ActivityType action, String object, String objectRef, String source) {
        super(source);
        this.action = action;
        this.object = object;
        this.objectRef = objectRef;
    }

    /**
     * @return the action
     */
    public ActivityType getAction() {
        return action;
    }

    /**
     * @return the object
     */
    public String getObject() {
        return object;
    }

    /**
     * @return the objectRef
     */
    public String getObjectRef() {
        return objectRef;
    }
    
    @Override
    public String getSource(){
        return source.toString();
    }

}
