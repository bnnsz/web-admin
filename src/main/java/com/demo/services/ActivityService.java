/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.services;

import com.demo.dtos.Activity;
import com.demo.dtos.NarratedActivity;
import com.demo.enums.ActivityType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.demo.repositories.ActivityEntityRepository;

/**
 *
 * @author obinna.asuzu
 */
@Service
public class ActivityService {

    @Autowired
    ActivityEntityRepository activityEntityRepository;

    public List<Activity> getAllActivities() {
        return activityEntityRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")).stream().map(d -> new Activity(d))
                .collect(Collectors.toList());
    }

    public Page<Activity> getAllActivities(int page, int size) {
        return activityEntityRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))).map(d -> new Activity(d));
    }

    public List<Activity> getAllActivitiesByVerb(ActivityType verb) {
        return activityEntityRepository.findByVerb(verb, Sort.by(Sort.Direction.DESC, "timestamp")).stream().map(d -> new Activity(d))
                .collect(Collectors.toList());
    }

    public List<NarratedActivity> getNarratedActivitiesBySubject(String subject) {
        return activityEntityRepository.findBySubject(subject, Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream()
                .map(NarratedActivity::create)
                .collect(Collectors.toList());
    }

    public Page<NarratedActivity> getNarratedActivitiesBySubject(String subject, int page, int size) {
        return activityEntityRepository.findBySubject(subject, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")))
                .map(NarratedActivity::create);
    }

    public List<NarratedActivity> getNarratedActivitiesBySubjectAndVerb(String subject, ActivityType verb) {
        return activityEntityRepository.findBySubjectAndVerb(subject, verb, Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream()
                .map(NarratedActivity::create)
                .collect(Collectors.toList());
    }

    public Page<NarratedActivity> getNarratedActivitiesBySubjectAndVerb(String subject, ActivityType verb, int page, int size) {
        return activityEntityRepository.findBySubjectAndVerb(subject, verb, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")))
                .map(NarratedActivity::create);
    }

    public Page<Activity> getAllActivitiesByVerb(
            ActivityType verb,
            String search,
            LocalDateTime from,
            LocalDateTime to,
            int page, int size) {

        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;
        if (search != null && !search.trim().isEmpty()) {
            if (verb == null) {
                return activityEntityRepository
                        .seachByCriteria(search, from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")))
                        .map(d -> new Activity(d));
            }
            return activityEntityRepository
                    .seachByCriteriaAndVerb(verb, search, from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")))
                    .map(d -> new Activity(d));
        }

        if (verb == null) {
            return activityEntityRepository
                    .findByRange(from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")))
                    .map(d -> new Activity(d));
        }
        return activityEntityRepository
                .findByVerbAndRange(verb, from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")))
                .map(d -> new Activity(d));
    }
}
