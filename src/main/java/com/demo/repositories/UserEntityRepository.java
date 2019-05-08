/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.repositories;

import com.demo.entities.UserEntity;
import com.demo.enums.ActivityType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author obinna.asuzu
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByUsername(String username);
    
    
    @Query("SELECT DISTINCT a FROM UserEntity a JOIN a.principals p "
            + "WHERE (a.username LIKE %:criteria% "
            + "OR p.value LIKE %:criteria%) "
            + "AND (a.createdTimestamp BETWEEN :fromDate AND :toDate)")
    public Page<UserEntity> searchByCriteria(
            @Param("criteria") String criteria,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
    
    @Query("SELECT a FROM UserEntity a "
            + "WHERE (a.createdTimestamp BETWEEN :fromDate AND :toDate)")
    public Page<UserEntity> findByRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
    
    @Query("SELECT count(a.username) FROM UserEntity a "
            + "WHERE (a.createdTimestamp BETWEEN :fromDate AND :toDate)")
    public int count(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT count(DISTINCT a.username) FROM UserEntity a, ActivityEntity p "
            + "WHERE (a.username = p.subject "
            + "AND p.verb = :verb "
            + "AND p.timestamp BETWEEN :fromDate AND :toDate)")
    public int countUsersByActivity(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("verb") ActivityType verb);
    
    @Query("SELECT count(DISTINCT a.username) FROM UserEntity a, ActivityEntity p "
            + "WHERE (a.username = p.subject "
            + "AND p.verb = :verb)")
    public int countUsersByActivity(
            @Param("verb") ActivityType verb);

    @Query("SELECT DISTINCT a FROM UserEntity a JOIN a.principals p "
            + "WHERE (p.value = :email)")
    public List<UserEntity> findByEmail(@Param("email") String email);
    
    @Query("SELECT DISTINCT a FROM UserEntity a JOIN a.principals p "
            + "WHERE (a.username = :username OR p.value = :email)")
    public List<UserEntity> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
}
