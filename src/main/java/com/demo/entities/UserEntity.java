/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.entities;

import com.demo.utils.CredentialConverter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author obinna.asuzu
 */
@Entity
public class UserEntity extends AbstractEntity implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    @Convert(converter = CredentialConverter.class)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<RoleEntity> roles = new HashSet<>();

    @Column
    private boolean enabled;

    @Column
    private boolean accountNonLocked;

    @Column
    private boolean credentialsNonExpired;

    @Column
    private boolean accountNonExpired;

    @Column
    private boolean system;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserPrincipalEntity> principals = new HashSet<>();

    private transient final List<GrantedAuthority> authorities = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(Long id, String username, String password, boolean enabled, boolean accountNonLocked, boolean credentialsNonExpired, boolean accountNonExpired) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonExpired = accountNonExpired;
    }

    public UserEntity edit(UserDetails userDetais) {
        this.id = id;
        this.username = userDetais.getUsername();
        this.password = userDetais.getPassword();
        this.enabled = userDetais.isEnabled();
        this.accountNonLocked = userDetais.isAccountNonLocked();
        this.credentialsNonExpired = userDetais.isCredentialsNonExpired();
        this.accountNonExpired = userDetais.isAccountNonExpired();
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserEntity)) {
            return false;
        }
        UserEntity other = (UserEntity) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.demo.data.entities.UserEntity[ id=" + id + " ]";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities.isEmpty()) {
            roles.forEach(this::addToAuthority);
        }
        return authorities;
    }

    private void addToAuthority(RoleEntity role) {
        authorities.add((GrantedAuthority) () -> "ROLE_" + role.getName());
        role.getPrivileges().forEach(priv -> {
            if (priv.isWrite()) {
                authorities.add((GrantedAuthority) () -> role.getName() + "." + priv.getValue() + "_WRITE");
            }

            if (priv.isRead()) {
                authorities.add((GrantedAuthority) () -> role.getName() + "." + priv.getValue() + "_READ");
            }
        });
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @return the roles
     */
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param accountNonLocked the accountNonLocked to set
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * @param credentialsNonExpired the credentialsNonExpired to set
     */
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * @param accountNonExpired the accountNonExpired to set
     */
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * @return the system
     */
    public boolean isSystem() {
        return system;
    }

    /**
     * @param system the system to set
     */
    public void setSystem(boolean system) {
        this.system = system;
    }

    /**
     * @return the principals
     */
    public Set<UserPrincipalEntity> getPrincipals() {
        return principals;
    }

    /**
     * @param principals the principals to set
     */
    public void setPrincipals(Set<UserPrincipalEntity> principals) {
        this.principals = principals;
    }

    /**
     * @param key
     * @param value
     */
    public void setPrincipal(String key, String value) {
        boolean[] found = {false};
        this.principals.stream().filter(p -> p.getKey() != null && p.getKey().equals(key)).findFirst().ifPresent(p -> {
            p.setValue(value);
            found[0] = true;
        });
        if (!found[0]) {
            principals.add(new UserPrincipalEntity(this, null, key, value));
        }
    }

    @Override
    public String getDescription() {
        return "User: " + username;
    }

}
