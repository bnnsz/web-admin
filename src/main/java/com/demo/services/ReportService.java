/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.services;

import com.demo.enums.ActivityType;
import com.demo.repositories.UserEntityRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author obinna.asuzu
 */
@Service
public class ReportService {

    @Autowired
    UserEntityRepository userEntityRepository;

    public long getTotalUsers(LocalDateTime fromDate, LocalDateTime toDate) {
        return userEntityRepository.count(fromDate, toDate);
    }

    public long getTotalUsers() {
        return userEntityRepository.count();
    }

    public long getTotalActiveUsers() {
        return userEntityRepository.countUsersByActivity(ActivityType.LOGGED_IN);
    }

    public long getTotalActiveUsers(LocalDateTime fromDate, LocalDateTime toDate) {
        return userEntityRepository.countUsersByActivity(fromDate, toDate, ActivityType.LOGGED_IN);
    }
}
