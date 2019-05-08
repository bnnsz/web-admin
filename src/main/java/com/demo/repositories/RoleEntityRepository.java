/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.repositories;

import com.demo.entities.RoleEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author obinna.asuzu
 */
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {

    public Optional<RoleEntity> findByName(String name);

    public List<RoleEntity> deleteByName(String name);
    
     @Query("SELECT a FROM RoleEntity a "
            + "WHERE a.name IN (:names)")
    public List<RoleEntity> findByNames(@Param("names")  List<String> names);

    
    @Query("SELECT a FROM RoleEntity a "
            + "WHERE a.name LIKE %:criteria% "
            + "AND (a.createdTimestamp BETWEEN :fromDate AND :toDate)")
    public Page<RoleEntity> searchByCriteria(
            @Param("criteria") String criteria,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT a FROM RoleEntity a "
            + "WHERE (a.createdTimestamp BETWEEN :fromDate AND :toDate)")
    public Page<RoleEntity> findByRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
