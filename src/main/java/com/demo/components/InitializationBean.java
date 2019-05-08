/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.components;

import com.demo.entities.GrantedPrivilegeEntity;
import com.demo.entities.PrivilegeEntity;
import com.demo.entities.RoleEntity;
import com.demo.entities.TokenEntity;
import com.demo.entities.UserEntity;
import com.demo.repositories.GrantedPrivilegeEntityRepository;
import com.demo.repositories.PrivilegeEntityRepository;
import com.demo.repositories.RoleEntityRepository;
import com.demo.repositories.TokenEntityRepository;
import com.demo.repositories.UserEntityRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 *
 * @author obinna.asuzu
 */
@Component
public class InitializationBean implements
  ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    UserEntityRepository userEntityRepository;
    @Autowired
    PrivilegeEntityRepository privilegeEntityRepository;
    @Autowired
    GrantedPrivilegeEntityRepository grantedPrivilegeEntityRepository;
    @Autowired
    RoleEntityRepository roleEntityRepository;
    @Autowired
    TokenEntityRepository tokenEntityRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    
    public void init(){
        try {
            initRoles();
            initPrivileges();
            initGrants();
            initUsers();
        } catch (Exception ex) {
            Logger.getLogger(InitializationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initRoles() {
        if (roleEntityRepository.count() == 0) {
            Arrays.asList("ADMIN", "VENDOR", "CLIENT", "SUPPLIER")
                    .forEach((role) -> roleEntityRepository.save(new RoleEntity(role,true)));
        }
    }

    public void initPrivileges() {
        if (privilegeEntityRepository.count() == 0) {
            Arrays.asList("USER", "EMAIL", "SMS", "SWAGGER", "ACTUATOR", "LOG")
                    .forEach((priv) -> privilegeEntityRepository.save(new PrivilegeEntity(priv,true)));
        }
    }

    public void initGrants() throws Exception {
        for (String role : Arrays.asList("ADMIN", "VENDOR", "CLIENT", "SUPPLIER")) {
            Optional<RoleEntity> option = roleEntityRepository.findByName(role);
            RoleEntity roleEntity = option.orElseThrow(() -> new Exception());
            List<String> privileges = new ArrayList<>();
            switch (role) {
                case "ADMIN":
                    privileges.addAll(Arrays.asList("SWAGGER", "ACTUATOR", "LOG"));
                    break;
            }
            privileges.addAll(Arrays.asList("USER", "EMAIL", "SMS"));

            for (String privilege : privileges) {
                GrantedPrivilegeEntity gpe = new GrantedPrivilegeEntity(privilege, true, true);
                gpe = grantedPrivilegeEntityRepository.save(gpe);
                gpe.setRole(roleEntity);
                roleEntity.getPrivileges().add(gpe);
                roleEntityRepository.save(roleEntity);
            }

        };
    }

    public void initUsers() throws Exception {
        if (userEntityRepository.count() == 0) {
            RoleEntity role = roleEntityRepository.findByName("ADMIN").orElseThrow(() -> new Exception());
            UserEntity user = new UserEntity();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setPrincipal("firstname", "Admin");
            user.setCredentialsNonExpired(false);
            user.setEnabled(true);
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setSystem(true);
            user = userEntityRepository.save(user);
            user.getRoles().add(role);
            role.getUsers().add(user);
            userEntityRepository.save(user);
            
            TokenEntity token = new TokenEntity(jwtTokenUtil.doGenerateToken(user), user);
            token.setExpired(false);
            tokenEntityRepository.save(token);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent e) {
        init();
    }
}
