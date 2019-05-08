/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.services;

import com.demo.components.JwtTokenUtil;
import com.demo.dtos.Address;
import com.demo.dtos.UserDto;
import com.demo.entities.RoleEntity;
import com.demo.entities.TokenEntity;
import com.demo.entities.UserEntity;
import com.demo.entities.UserPrincipalEntity;
import com.demo.enums.Error;
import com.demo.exceptions.ServiceException;
import com.demo.repositories.GrantedPrivilegeEntityRepository;
import com.demo.repositories.PrivilegeEntityRepository;
import com.demo.repositories.RoleEntityRepository;
import com.demo.repositories.TokenEntityRepository;
import com.demo.repositories.UserEntityRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author obinna.asuzu
 */
@Service
public class UserService {

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    TokenEntityRepository tokenEntityRepository;

    @Autowired
    RoleEntityRepository roleEntityRepository;

    @Autowired
    PrivilegeEntityRepository privilegeEntityRepository;

    @Autowired
    GrantedPrivilegeEntityRepository grantedPrivilegeEntityRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public UserEntity activateUser(String tokenValue) throws Exception, UsernameNotFoundException {
        Optional<TokenEntity> tokenOption = tokenEntityRepository.findByValue(tokenValue);
        if (tokenOption.isPresent()) {
            TokenEntity token = tokenOption.get();
            if (token.isExpired()) {
                throw new ServiceException(Error.token_expired);
            }
            UserEntity user = token.getUser();

            if (user == null) {
                throw new ServiceException(Error.token_invalid);
            }

            if (user.isEnabled()) {
                throw new ServiceException(Error.account_active);
            }

            user.setEnabled(true);

            user = userEntityRepository.save(user);
            return user;
        } else {
            throw new ServiceException(Error.token_invalid);
        }
    }

    public UserDetails verifyToken(String value) throws Exception, UsernameNotFoundException {
        TokenEntity token = tokenEntityRepository.findByValue(value)
                .orElseThrow(() -> new ServiceException(Error.token_invalid));
        if (token.isExpired()) {
            throw new ServiceException(Error.token_expired);
        }

        if (token.getUser() == null) {
            throw new ServiceException(Error.token_invalid);
        }

        UserEntity user = token.getUser();

        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("Invalid username or password");
        }

        if (!jwtTokenUtil.validateToken(value, user)) {
            throw new ServiceException(Error.token_invalid);
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("Account has expired");
        }

        if (!user.isAccountNonLocked()) {
            throw new LockedException("Account has been locked");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("Account has been disabled");
        }

        return user;
    }

    public UserEntity getUser(String username) throws ServiceException {
        return userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(Error.user_not_exist));
    }

    @Transactional
    public String requestToken(String username) throws ServiceException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(Error.user_not_exist));

        return tokenEntityRepository.findByUserAndExpired(user, false)
                .peek(t -> t.setExpired(true))
                .peek(t -> tokenEntityRepository.save(t))
                .filter(t -> !t.isExpired())
                .findFirst()
                .orElseGet(() -> {
                    TokenEntity token = new TokenEntity(jwtTokenUtil.doGenerateToken(user), user);
                    token.setExpired(false);
                    tokenEntityRepository.save(token);
                    return token;
                }).getValue();
    }

    public Map<String, String> getUserProfile(String username) throws ServiceException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(Error.user_not_exist));
        return user.getPrincipals().stream().collect(toMap(UserPrincipalEntity::getKey, UserPrincipalEntity::getValue));
    }

    public UserEntity registerUser(
            String username,
            String firstname,
            String lastname,
            String email,
            String phone,
            List<String> roles) throws ServiceException, Exception {
        UserEntity user = createUser(username, firstname, lastname, email, phone, roles);
        if (user != null) {
            String tokenString = jwtTokenUtil.doGenerateToken(user);
            TokenEntity token = new TokenEntity(tokenString, user);
            tokenEntityRepository.save(token);

            emailService.sendWelcomeMessage(new Address(email), user, tokenString);
        }
        return user;
    }

    public UserEntity createUser(
            String username,
            String firstname,
            String lastname,
            String email,
            String phone,
            List<String> roles) throws ServiceException {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(false);
        user.setEnabled(false);

        Map<String, String> principles = new HashMap<>();
        principles.put("username", username);
        principles.put("firstname", firstname);
        principles.put("lastname", lastname);
        principles.put("email", email);
        principles.put("phone", phone);

        principles.entrySet().forEach(entry -> {
            user.setPrincipal(entry.getKey(), entry.getValue());
        });

        if (!userEntityRepository.findByUsernameOrEmail(username, email).isEmpty()) {
            throw new ServiceException(Error.user_exist);
        };

        userEntityRepository.save(user);

        roleEntityRepository.findByNames(roles).forEach(r -> {
            if (!r.getUsers().contains(user)) {
                r.getUsers().add(user);
            }

            if (!user.getRoles().contains(r)) {
                user.getRoles().add(r);
            }
        });

        userEntityRepository.save(user);
        return user;
    }

    public boolean deactivateUser(String username) throws ServiceException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(Error.user_not_exist));
        if (user.isSystem()) {
            throw new ServiceException(Error.cannot_deactivate_sys_user);
        }
        user.setEnabled(false);
        userEntityRepository.save(user);
        return true;
    }

    public String changeUserPassword(String username, String newPassword) throws ServiceException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(Error.user_not_exist));
        user.setPassword(newPassword);
        userEntityRepository.save(user);
        return user.getPassword();
    }

    public List<UserEntity> getAllUsers() {
        return userEntityRepository.findAll();
    }

    public Page<UserEntity> getAllUsers(int page, int size) {
        return userEntityRepository.findAll(PageRequest.of(page, size));
    }

    public Page<UserDto> getAllUsers(
            String search,
            LocalDateTime from,
            LocalDateTime to,
            int page, int size) {

        System.out.println("--->" + size);
        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;
        if (search != null && !search.trim().isEmpty()) {
            return userEntityRepository
                    .searchByCriteria(search, from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTimestamp")))
                    .map(d -> new UserDto(d));
        }

        return userEntityRepository
                .findByRange(from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTimestamp")))
                .map(d -> new UserDto(d));
    }

    public List<UserEntity> getAllUsersByRole(String role) {
        Optional<RoleEntity> roleEntity = roleEntityRepository.findByName(role);
        if (roleEntity.isPresent()) {
            return new ArrayList<>(roleEntity.get().getUsers());
        }
        return new ArrayList<>();
    }

    public List<UserEntity> getAllUsersByPrivilege(String privilege) {
        return grantedPrivilegeEntityRepository.findByValue(privilege)
                .stream()
                .map(p -> p.getRole())
                .map(r -> r.getUsers())
                .flatMap(r -> r.stream())
                .collect(Collectors.toList());
    }

    public UserEntity updateUserProfile(String username, Map<String, String> principles) throws ServiceException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(Error.user_not_exist));

        principles.entrySet().forEach(entry -> {
            user.setPrincipal(entry.getKey(), entry.getValue());
        });

        return userEntityRepository.save(user);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) throws ServiceException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(Error.user_not_exist));
        if (user.getPassword() == null && oldPassword == null) {
            user.setPassword(newPassword);
            user.setCredentialsNonExpired(true);
        } else if (user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            user.setCredentialsNonExpired(true);
        }
        tokenEntityRepository.expireAllByUser(user);
        return userEntityRepository.save(user).getPassword().equals(newPassword);
    }

}
