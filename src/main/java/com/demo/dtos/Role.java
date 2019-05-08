/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

import com.demo.entities.GrantedPrivilegeEntity;
import com.demo.entities.RoleEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author obinna.asuzu
 */
public class Role {

    private String value;
    private List<String> grantedPrivileges = new ArrayList<>();
    
    private List<Link> actions = new ArrayList<>();

    public Role() {
    }

    public Role(RoleEntity entity) {
        this.value = entity.getName();
        entity.getPrivileges().forEach(this::addGrant);
        
    }

    private void addGrant(GrantedPrivilegeEntity priv) {
        if (priv.isRead()) {
            grantedPrivileges.add(priv.getValue().toUpperCase() + "_READ");
        }
        if (priv.isWrite()) {
            grantedPrivileges.add(priv.getValue().toUpperCase() + "_WRITE");
        }
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

    /**
     * @return the grantedPrivileges
     */
    public List<String> getGrantedPrivileges() {
        return grantedPrivileges;
    }

    /**
     * @param grantedPrivileges the grantedPrivileges to set
     */
    public void setGrantedPrivileges(List<String> grantedPrivileges) {
        this.grantedPrivileges = grantedPrivileges;
    }

    /**
     * @return the actions
     */
    public List<Link> getActions() {
        return actions;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(List<Link> actions) {
        this.actions = actions;
    }

}
