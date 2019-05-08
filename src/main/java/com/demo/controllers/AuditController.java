/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.controllers;

import com.demo.dtos.NarratedActivity;
import com.demo.dtos.Activity;
import com.demo.dtos.Pagination;
import com.demo.dtos.Search;
import com.demo.enums.ActivityType;
import com.demo.services.ActivityService;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author obinna.asuzu
 */
@Controller
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    ActivityService activityService;

    String gotoAuditListPaginated(Model model) {
        return gotoAuditListPaginated(model, null, null, null, null, null, null);
    }

    public enum Action {
        all(null), created(ActivityType.CREATED), updated(ActivityType.UPDATED), removed(ActivityType.REMOVED);
        ActivityType entityAction;

        private Action(ActivityType action) {
            this.entityAction = action;
        }

    };

    @RequestMapping("")
    public String gotoAuditListPaginated(Model model,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Action action,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate,
            @RequestParam(required = false, name = "s") String search) {

        action = action == null ? Action.all : action;
        page = page == null ? 1 : page;
        count = count == null ? 10 : count;
        LocalDateTime to = toLocalDateTime(toDate);
        LocalDateTime from = toLocalDateTime(fromDate);
        search = search == null ? "" : search;

        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;

        Page<Activity> data = activityService
                .getAllActivitiesByVerb(action.entityAction, search, from, to, page - 1, count);

        int totalPages = data.getTotalPages() + 1;
        int currentPage = data.getNumber() + 1;
        List<Activity> audit = data.getContent();

        List<Pagination> pagination = IntStream.range(1, totalPages)
                .mapToObj(pageNo -> new Pagination(pageNo, pageNo == currentPage))
                .filter(p -> byRange(3, currentPage, p))
                .collect(Collectors.toList());

        model.addAttribute("audit", audit);
        model.addAttribute("pagination", pagination);

        model.addAttribute("searchForm", new Search(search));
        model.addAttribute("search", search);
        model.addAttribute("searchQuery", "s=" + search);
        model.addAttribute("from", toDate(from));
        model.addAttribute("to", toDate(to));

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", totalPages - 1);
        model.addAttribute("pageCount", count);
        model.addAttribute("isFirstPage", data.isFirst());
        model.addAttribute("isLastPage", data.isLast());
        model.addAttribute("action", action);

        return "audit.html";
    }

    

    @RequestMapping("/search")
    public String searchAuditListPaginated(
            Model model,
            @ModelAttribute Search searchForm,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Action action,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate) {

        action = action == null ? Action.all : action;
        page = page == null ? 1 : page;
        count = count == null ? 10 : count;
        LocalDateTime to = toLocalDateTime(toDate);
        LocalDateTime from = toLocalDateTime(fromDate);
        String search = searchForm.getValue() == null ? "" : searchForm.getValue();

        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;

        Page<Activity> data = activityService
                .getAllActivitiesByVerb(action.entityAction, search, from, to, page - 1, count);

        int totalPages = data.getTotalPages() + 1;
        int currentPage = data.getNumber() + 1;
        List<Activity> audit = data.getContent();

        List<Pagination> pagination = IntStream.range(1, totalPages)
                .mapToObj(pageNo -> new Pagination(pageNo, pageNo == currentPage))
                .filter(p -> byRange(3, currentPage, p))
                .collect(Collectors.toList());

        model.addAttribute("audit", audit);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchForm", new Search(search));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", totalPages - 1);
        model.addAttribute("pageCount", count);
        model.addAttribute("isFirstPage", data.isFirst());
        model.addAttribute("isLastPage", data.isLast());
        model.addAttribute("action", action);
        model.addAttribute("search", search);
        model.addAttribute("searchQuery", "s=" + search);
        model.addAttribute("from", toDate(from));
        model.addAttribute("to", toDate(to));
        return "audit.html";
    }
    
    
    
    @RequestMapping("/{username}")
    public String gotoAuditListPaginated(
            Model model,
            @PathVariable String username,
            @RequestParam(required = false) AuditController.Action action,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count) {

        Page<NarratedActivity> data;
        if (action == null || action == AuditController.Action.all) {
            data = activityService.getNarratedActivitiesBySubject(username, page - 1, count);
        } else {
            data = activityService.getNarratedActivitiesBySubjectAndVerb(username, action.entityAction, page - 1, count);
        }

        int totalPages = data.getTotalPages() + 1;
        int currentPage = data.getNumber() + 1;
        List<NarratedActivity> audit = data.getContent();

        List<Pagination> pagination = IntStream.range(1, totalPages)
                .mapToObj(pageNo -> new Pagination(pageNo, pageNo == currentPage))
                .filter(p -> byRange(3, currentPage, p))
                .collect(Collectors.toList());

        model.addAttribute("audit", audit);
        model.addAttribute("pagination", pagination);

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", totalPages - 1);
        model.addAttribute("pageCount", count);
        model.addAttribute("isFirstPage", data.isFirst());
        model.addAttribute("isLastPage", data.isLast());
        model.addAttribute("action", action);

        return "user-activities.html";
    }

    private boolean byRange(int range, int central, Pagination p) {
        return p.getPageNo() >= (central - range) && p.getPageNo() <= (central + range);
    }

    public LocalDateTime toLocalDateTime(String dateToConvert) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(dateToConvert);
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toDate(LocalDateTime dateToConvert) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        return dateToConvert.format(formatter);
    }

}
