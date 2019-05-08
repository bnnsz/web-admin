/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.repositories;

import com.demo.entities.TokenEntity;
import com.demo.entities.UserEntity;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author obinna.asuzu
 */
public interface TokenEntityRepository extends JpaRepository<TokenEntity, Long> {

    public Optional<TokenEntity> findByValue(String value);

    public Stream<TokenEntity> findByUser_Username(String username);

    public Stream<TokenEntity> findByUser_Id(Long id);

    public Stream<TokenEntity> findByUser(UserEntity user);

    public Stream<TokenEntity> findByUserAndExpired(UserEntity user, boolean expired);

    @Transactional
    @Modifying
    @Query("UPDATE TokenEntity SET expired = true WHERE user = :user")
    public void expireAllByUser(@Param("user") UserEntity user);
}
