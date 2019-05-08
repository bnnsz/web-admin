/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.repositories;

import com.demo.entities.PrivilegeEntity;
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
public interface PrivilegeEntityRepository extends JpaRepository<PrivilegeEntity, Long> {

    public Optional<PrivilegeEntity> findByValue(String value);

    public List<PrivilegeEntity> deleteByValue(String value);

    @Query("SELECT a FROM PrivilegeEntity a "
            + "WHERE a.value LIKE %:criteria% "
            + "AND (a.createdTimestamp BETWEEN :fromDate AND :toDate)")
    public Page<PrivilegeEntity> searchByCriteria(
            @Param("criteria") String criteria,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT a FROM PrivilegeEntity a "
            + "WHERE (a.createdTimestamp BETWEEN :fromDate AND :toDate)")
    public Page<PrivilegeEntity> findByRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

}
