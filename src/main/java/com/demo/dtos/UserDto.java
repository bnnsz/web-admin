/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

import com.demo.entities.UserEntity;
import com.demo.entities.UserPrincipalEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Objects;
import static java.util.stream.Collectors.toMap;
import org.springframework.security.core.AuthenticatedPrincipal;

/**
 *
 * @author obinna.asuzu
 */
@JsonInclude(Include.NON_NULL)
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1009L;
    private String username;
    private String password;
    private Map<String, String> principals = new HashMap<>();
    private List<String> grantedAuthorities = new ArrayList<>();
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean accountNonExpired;
    private Boolean system;
    private List<String> roles = new ArrayList<>();
    
    private Map<String, String> links = new HashMap<>();

    public UserDto() {

    }

    public UserDto(UserDetails other) {
        this.username = other.getUsername();
//        this.password = other.getPassword();
        this.grantedAuthorities = other.getAuthorities().stream().map(g -> g.getAuthority()).collect(Collectors.toList());
        this.accountNonExpired = other.isAccountNonExpired();
        this.accountNonLocked = other.isAccountNonLocked();
        this.credentialsNonExpired = other.isCredentialsNonExpired();
        this.enabled = other.isEnabled();
        this.roles = this.grantedAuthorities.stream().filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.split("_")[1])
                .collect(Collectors.toList());
    }

    public UserDto(UserEntity other) {
        this.username = other.getUsername();
//        this.password = other.getPassword();
        this.grantedAuthorities = other.getAuthorities().stream().map(g -> g.getAuthority()).collect(Collectors.toList());
        this.accountNonExpired = other.isAccountNonExpired();
        this.accountNonLocked = other.isAccountNonLocked();
        this.credentialsNonExpired = other.isCredentialsNonExpired();
        this.enabled = other.isEnabled();
        this.principals = other.getPrincipals().stream().collect(toMap(UserPrincipalEntity::getKey, UserPrincipalEntity::getValue));
        this.system = other.isSystem();
        this.roles = this.grantedAuthorities.stream().filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.split("_")[1])
                .collect(Collectors.toList());
        if(!other.isEnabled()){
            this.links.put("activate", "/users/"+other.getUsername()+"/activate");
        }else{
            this.links.put("disable", "/users/"+other.getUsername()+"/disable");
        }
        this.links.put("profile", "/users/"+other.getUsername()+"/profile");
        
    }

    public UserDto(String username) {
        this.username = username;
        this.principals = null;
        this.roles = null;
        this.grantedAuthorities = null;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public Boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public Boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the principals
     */
    public Map<String, String> getPrincipals() {
        return principals;
    }

    /**
     * @param principals the principals to set
     */
    public void setPrincipals(Map<String, String> principals) {
        this.principals = principals;
    }

    /**
     * @return the grantedAuthorities
     */
    public List<String> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    /**
     * @param grantedAuthorities the grantedAuthorities to set
     */
    public void setGrantedAuthorities(List<String> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param accountNonLocked the accountNonLocked to set
     */
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * @param credentialsNonExpired the credentialsNonExpired to set
     */
    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * @param accountNonExpired the accountNonExpired to set
     */
    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * @return the system
     */
    public Boolean isSystem() {
        return system;
    }

    /**
     * @param system the system to set
     */
    public void setSystem(Boolean system) {
        this.system = system;
    }

    /**
     * @return the roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean validate(UserDto userDetails) {
        String otherUser = username + ":" + grantedAuthorities + ":"
                + accountNonExpired + ":" + accountNonLocked + ":"
                + credentialsNonExpired + ":" + enabled;

        String thisUser = userDetails.username + ":" + userDetails.grantedAuthorities + ":"
                + userDetails.accountNonExpired + ":" + userDetails.accountNonLocked + ":"
                + userDetails.credentialsNonExpired + ":" + userDetails.enabled;

        return thisUser.equals(otherUser);
    }

    /**
     * @return the links
     */
    public Map<String, String> getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserDto other = (UserDto) obj;
        return Objects.equals(this.username, other.username);
    }
    
    
    

}
