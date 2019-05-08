/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.repositories;

import com.demo.entities.ActivityEntity;
import com.demo.enums.ActivityType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author obinna.asuzu
 */
public interface ActivityEntityRepository extends JpaRepository<ActivityEntity, Long> {

    public List<ActivityEntity> findByVerb(ActivityType verb);

    public Page<ActivityEntity> findByVerb(ActivityType verb, Pageable pageable);

    public List<ActivityEntity> findByVerb(ActivityType verb, Sort sort);

    public List<ActivityEntity> findBySubject(String subject);

    public Page<ActivityEntity> findBySubject(String subject, Pageable pageable);

    public List<ActivityEntity> findBySubject(String subject, Sort sort);

    public List<ActivityEntity> findBySubjectAndVerb(String subject, ActivityType verb);

    public Page<ActivityEntity> findBySubjectAndVerb(String subject, ActivityType verb, Pageable pageable);

    public List<ActivityEntity> findBySubjectAndVerb(String subject, ActivityType verb, Sort sort);

    @Query("SELECT a FROM ActivityEntity a "
            + "WHERE (a.objectDescription LIKE %:criteria% "
            + "OR a.subject LIKE %:criteria% "
            + "OR a.objectRef LIKE %:criteria%) "
            + "AND (a.timestamp BETWEEN :fromDate AND :toDate)")
    public Page<ActivityEntity> seachByCriteria(
            @Param("criteria") String criteria,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT a FROM ActivityEntity a "
            + "WHERE a.verb = :act "
            + "AND (a.objectDescription LIKE %:criteria% "
            + "OR a.subject LIKE %:criteria% "
            + "OR a.objectRef LIKE %:criteria%) "
            + "AND (a.timestamp BETWEEN :fromDate AND :toDate)")
    public Page<ActivityEntity> seachByCriteriaAndVerb(
            @Param("act") ActivityType verb,
            @Param("criteria") String criteria,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT a FROM ActivityEntity a "
            + "WHERE a.verb = :act "
            + "AND (a.timestamp BETWEEN :fromDate AND :toDate)")
    public Page<ActivityEntity> findByVerbAndRange(
            @Param("act") ActivityType verb,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT a FROM ActivityEntity a "
            + "WHERE (a.timestamp BETWEEN :fromDate AND :toDate)")
    public Page<ActivityEntity> findByRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
