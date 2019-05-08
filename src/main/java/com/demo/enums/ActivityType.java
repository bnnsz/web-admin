/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.enums;

/**
 *
 * @author ObinnaAsuzu
 */
public enum ActivityType {
    CREATED("created"),
    REMOVED("removed"),
    UPDATED("updated"),
    LOGGED_IN("logged in"),
    LOGGED_OUT("logged out");
    
    String value; 

    private ActivityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    
    
}
