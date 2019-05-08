/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.repositories;

import com.demo.entities.GrantedPrivilegeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author obinna.asuzu
 */
public interface GrantedPrivilegeEntityRepository extends JpaRepository<GrantedPrivilegeEntity, Long> {
    public List<GrantedPrivilegeEntity> findByValue(String value);
}
