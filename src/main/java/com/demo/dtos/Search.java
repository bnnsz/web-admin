/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

/**
 *
 * @author obinna.asuzu
 */
public class Search {
    private String value;

    public Search() {
    }

    public Search(String value) {
        this.value = value;
    }
    
    
    

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
