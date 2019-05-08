/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.controllers;

import com.demo.dtos.NarratedActivity;
import com.demo.dtos.Pagination;
import com.demo.dtos.Search;
import com.demo.dtos.UserDto;
import com.demo.exceptions.ServiceException;
import com.demo.forms.UserForm;
import com.demo.services.ActivityService;
import com.demo.services.ReportService;
import com.demo.services.UserService;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author obinna.asuzu
 */
@Controller
public class MainController {

    @Autowired
    ReportService reportService;
    
    @Autowired
    ActivityService activityService;

    @RequestMapping("/login")
    public String gotoLogin(Model model, Authentication authentication) {

        return "login.html";
    }

    @RequestMapping("/")
    public String gotoHome(Model model, Authentication authentication,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate) {

        String username = authentication.getName();
        LocalDateTime to = toLocalDateTime(toDate);
        LocalDateTime from = toLocalDateTime(fromDate);

        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;

        
        Page<NarratedActivity> activities = activityService.getNarratedActivitiesBySubject(username, 0, 10);
        

        model.addAttribute("activities", activities);
        addTotalValues(from, to, model);
        model.addAttribute("from", toDate(from));
        model.addAttribute("to", toDate(to));
        return "index.html";
    }

    private void addTotalValues(LocalDateTime from, LocalDateTime to, Model model) {
        long change = 0;
        long totalUsers = reportService.getTotalUsers(from, to);
        long totalUsersPrevWeek = reportService.getTotalUsers(from.minusWeeks(1), to.minusWeeks(1));
        if (totalUsersPrevWeek == 0) {
            change = totalUsers > 0 ? 100 : 0;
        } else {
            change = Math.abs(((totalUsers - totalUsersPrevWeek) / totalUsersPrevWeek) * 100);
        }

        long activeChange = 0;
        long totalActiveUsers = reportService.getTotalActiveUsers(from, to);
        long totalActiveUsersPrevWeek = reportService.getTotalActiveUsers(from.minusWeeks(1), to.minusWeeks(1));
        if (totalActiveUsersPrevWeek == 0) {
            activeChange = totalActiveUsers > 0 ? 100 : 0;
        } else {
            activeChange = Math.abs(((totalActiveUsers - totalActiveUsersPrevWeek) / totalActiveUsersPrevWeek) * 100);
        }

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("change", change);

        model.addAttribute("totalActiveUsers", totalActiveUsers);
        model.addAttribute("activeChange", activeChange);
    }

    private Integer validateCount(Integer count) {
        count = count == null ? 10 : count;
        return count;
    }

    private static int validatePage(Integer page) {
        return page == null ? 0 : page - 1;
    }

    private static String validateSearch(String search) {
        return search == null ? "" : search;
    }

    public LocalDateTime toLocalDateTime(String dateToConvert) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(dateToConvert);
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    public String toDate(LocalDateTime dateToConvert) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        return dateToConvert.format(formatter);
    }

}
