/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

import com.demo.entities.ActivityEntity;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author obinna.asuzu
 */
public class Activity {

    private String ref;
    private Date date;
    private String action;
    private String user;
    private String entity;
//    private String state;

    public Activity() {
    }

    public Activity(ActivityEntity entity) {
        ref = entity.getObjectRef();
        date = Date.from(entity.getTimestamp().atZone(ZoneId.systemDefault())
                        .toInstant());
        action = entity.getVerb().name();
        user = entity.getSubject();
        this.entity = entity.getObjectDescription();
//        state = entity.getEntityState();
    }

    /**
     * @return the ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * @return the state
     */
//    public String getState() {
//        return state;
//    }

    /**
     * @param state the state to set
     */
//    public void setState(String state) {
//        this.state = state;
//    }

}
