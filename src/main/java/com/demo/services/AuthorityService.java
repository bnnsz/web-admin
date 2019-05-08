/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.services;

import com.demo.dtos.Role;
import com.demo.entities.GrantedPrivilegeEntity;
import com.demo.entities.PrivilegeEntity;
import com.demo.entities.RoleEntity;
import com.demo.enums.Error;
import com.demo.exceptions.RecordExistsException;
import com.demo.exceptions.RecordNotFoundException;
import com.demo.exceptions.ServiceException;
import com.demo.repositories.GrantedPrivilegeEntityRepository;
import com.demo.repositories.PrivilegeEntityRepository;
import com.demo.repositories.RoleEntityRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 *
 * @author obinna.asuzu
 */
@Service
public class AuthorityService {

    @Autowired
    UserService userService;

    @Autowired
    RoleEntityRepository roleEntityRepository;

    @Autowired
    PrivilegeEntityRepository privilegeEntityRepository;

    @Autowired
    GrantedPrivilegeEntityRepository grantedPrivilegeEntityRepository;

    public List<String> getAllRoles(boolean indicateSystem) {
        return roleEntityRepository.findAll()
                .stream().map(p -> p.getName() + (indicateSystem ? p.isSystem() ? "*" : "" : ""))
                .collect(Collectors.toList());
    }

    public List<String> getAllRoles() {
        return getAllRoles(false);
    }

    public List<String> getAllRolesByPrivilege(String value, boolean indicateSystem) throws ServiceException {
        return grantedPrivilegeEntityRepository.findByValue(value)
                .stream()
                .map(p -> p.getRole().getName() + (indicateSystem ? p.getRole().isSystem() ? "*" : "" : ""))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAllRolesByPrivilege(String value) throws ServiceException {
        return getAllRolesByPrivilege(value, false);
    }

    public List<String> getAllAuthorities() {
        List<String> grantedAuthorities = new ArrayList<>();
        roleEntityRepository.findAll().forEach(role -> {
            role.getPrivileges().forEach(priv -> {
                String a = role.getName() + "." + priv.getValue();
                if (priv.isRead()) {
                    grantedAuthorities.add(a.toUpperCase() + "_READ");
                }

                if (priv.isWrite()) {
                    grantedAuthorities.add(a.toUpperCase() + "_WRITE");
                }
            });
        });
        return grantedAuthorities;
    }

    public List<String> getAllPermission(boolean indicateSystem) {
        return privilegeEntityRepository.findAll()
                .stream().map(p -> p.getValue() + (indicateSystem ? p.isSystem() ? "*" : "" : ""))
                .collect(Collectors.toList());
    }

    public List<String> getAllPermission() {
        return getAllPermission(false);
    }

    public List<String> getAllPermissionsByRole(String role, boolean indicateSystem) throws ServiceException {
        return roleEntityRepository.findByName(role)
                .orElseThrow(() -> new ServiceException(Error.role_not_exist))
                .getPrivileges().stream().map(p -> p.getValue() + (indicateSystem ? p.isSystem() ? "*" : "" : ""))
                .collect(Collectors.toList());
    }

    public List<String> getAllPermissionsByRole(String role) throws ServiceException {
        return getAllPermissionsByRole(role, false);
    }

    public RoleEntity createRole(Role role) throws Exception {
        if (roleEntityRepository.findByName(role.getValue()).isPresent()) {
            throw new ServiceException(Error.role_exist);
        }
        RoleEntity entity = new RoleEntity(role);
        return roleEntityRepository.save(entity);
    }

    private void validateAuthority(String value) throws ServiceException {
        value = value.trim();
        if (value.split("\\.").length != 2) {
            throw new ServiceException(Error.invalid_grant_format);
        }

        if (value.split("_").length != 2) {
            throw new ServiceException(Error.invalid_grant_format);
        }

        Pattern p = Pattern.compile("^[a-zA-Z0-9._-]*$", Pattern.CASE_INSENSITIVE);
        if (!p.matcher(value).matches()) {
            throw new ServiceException(Error.invalid_grant_characters);
        }

        if (!value.endsWith("_READ") && !value.endsWith("_WRITE")) {
            throw new ServiceException(Error.invalid_grant_access);
        }
    }

    private GrantedAuthority authorize(GrantedAuthority authority, boolean grant) throws RecordNotFoundException, Exception {
        String value = authority.getAuthority().toUpperCase();
        validateAuthority(value);

        String[] split = authority.getAuthority().split("\\.");

        String role = split[0];
        String privilege_access = split[1];

        split = privilege_access.split("_");

        String privilege = split[0];
        String access = split[1];

        RoleEntity roleEntity = roleEntityRepository.findByName(role)
                .orElseThrow(() -> new ServiceException(Error.role_not_exist));

        privilegeEntityRepository.findByValue(privilege)
                .orElseThrow(() -> new ServiceException(Error.privilege_not_exist));

        GrantedPrivilegeEntity grantedPrivilegeEntity = grantedPrivilegeEntityRepository.findByValue(privilege)
                .stream()
                .filter(p -> p.getRole().getName().equals(role))
                .findFirst().orElseGet(() -> {
                    return grantedPrivilegeEntityRepository.save(new GrantedPrivilegeEntity(privilege));
                });

        grantedPrivilegeEntity.setRead(access.equals("READ") ? grant : grantedPrivilegeEntity.isRead());
        grantedPrivilegeEntity.setWrite(access.equals("WRITE") ? grant : grantedPrivilegeEntity.isWrite());

        grantedPrivilegeEntity.setRole(roleEntity);
        roleEntity.getPrivileges().add(grantedPrivilegeEntity);
        roleEntityRepository.save(roleEntity);

        return authority;
    }

    public GrantedAuthority createAuthority(GrantedAuthority authority) throws RecordExistsException, RecordNotFoundException, Exception {
        return authorize(authority, true);
    }

    public void removeAuthority(GrantedAuthority authority) throws RecordNotFoundException, Exception {
        authorize(authority, false);
    }

    public int deleteRole(String name) throws Exception {
        if (!userService.getAllUsersByRole(name).isEmpty()) {
            throw new ServiceException(Error.cannot_del_roles_inuse);
        }
        RoleEntity role = roleEntityRepository.findByName(name).orElseThrow(() -> new ServiceException(Error.role_not_exist));
        if (role.isSystem()) {
            throw new ServiceException(Error.cannot_delete_sys_role);
        }
        return roleEntityRepository.deleteByName(name).size();
    }

    public int deletePrivilege(String value) throws Exception {
        if (!getAllRolesByPrivilege(value).isEmpty()) {
            throw new ServiceException(Error.cannot_del_privileges_inuse);
        }
        PrivilegeEntity privilege = privilegeEntityRepository.findByValue(value).orElseThrow(() -> new ServiceException(Error.privilege_not_exist));
        if (privilege.isSystem()) {
            throw new ServiceException(Error.cannot_delete_sys_privileges);
        }
        return privilegeEntityRepository.deleteByValue(value).size();
    }

    public PrivilegeEntity createPrivilege(String value) throws Exception {
        if (privilegeEntityRepository.findByValue(value).isPresent()) {
            throw new ServiceException(Error.privilege_exist);
        }

        PrivilegeEntity privilegeEntity = new PrivilegeEntity();
        privilegeEntity.setValue(value);
        return privilegeEntityRepository.save(privilegeEntity);
    }

    public Page<String> getAllPrivileges(String search, LocalDateTime from, LocalDateTime to, Integer page, Integer count) {
        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;
        if (search != null && !search.trim().isEmpty()) {
            return privilegeEntityRepository
                    .searchByCriteria(search, from, to, PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "createdTimestamp")))
                    .map(PrivilegeEntity::getValue);
        }

        return privilegeEntityRepository
                .findByRange(from, to, PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "createdTimestamp")))
                .map(PrivilegeEntity::getValue);
    }

    public Page<Role> getAllRoles(String search, LocalDateTime from, LocalDateTime to, Integer page, Integer count) {
        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;
        if (search != null && !search.trim().isEmpty()) {
            return roleEntityRepository
                    .searchByCriteria(search, from, to, PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "createdTimestamp")))
                    .map(Role::new);
        }

        return roleEntityRepository
                .findByRange(from, to, PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "createdTimestamp")))
                .map(Role::new);
    }

}
