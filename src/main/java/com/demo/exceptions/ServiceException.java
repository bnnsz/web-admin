/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.exceptions;

import com.demo.enums.Error;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author obinna.asuzu
 */
public class ServiceException extends Exception{
    Error error;
    private Map<String, Object> attribute = new HashMap<>();
 
    public ServiceException(Error code) {
        this.error = code;
    }

    @Override
    public String getMessage() {
        return error.getMessage();
    }
    
    public Error getError() {
        return error;
    }

    /**
     * @return the attribute
     */
    public Map<String, Object> getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }
    
    
    public void addAttribute(String key, Object value){
        this.attribute.put(key, value);
    }
}
