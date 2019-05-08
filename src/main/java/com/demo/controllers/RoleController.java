/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.controllers;

import com.demo.dtos.ErrorResponse;
import com.demo.dtos.Pagination;
import com.demo.dtos.Role;
import com.demo.dtos.Search;
import com.demo.entities.RoleEntity;
import com.demo.exceptions.ServiceException;
import com.demo.services.AuthorityService;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author obinna.asuzu
 */
@Controller
@RequestMapping("/roles")
public class RoleController {
    
    @Autowired
    AuthorityService authorityService;

    Authentication authentication;

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping()
    public String list(Model model,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate,
            @RequestParam(required = false, name = "s") String search) {

        page = validatePage(page);
        count = validateCount(count);
        LocalDateTime to = toLocalDateTime(toDate);
        LocalDateTime from = toLocalDateTime(fromDate);
        search = validateSearch(search);

        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;

        Page<Role> data = authorityService.getAllRoles(search,from, to, page, count);

        int totalPages = data.getTotalPages();
        int currentPage = data.getNumber() + 1;
        List<Role> roles = data.getContent();
        
        List<String> grants = new ArrayList<>();
        authorityService.getAllPermission().forEach(p -> {
            grants.add(p.toUpperCase()+"_READ");
            grants.add(p.toUpperCase()+"_WRITE");
        });
        

        List<Pagination> pagination = IntStream.range(1, totalPages + 1)
                .mapToObj(pageNo -> new Pagination(pageNo, pageNo == currentPage))
                .collect(Collectors.toList());

        model.addAttribute("roleForm", new Role());
        model.addAttribute("roles", roles);
        model.addAttribute("grants", grants);
        model.addAttribute("pagination", pagination);
        model.addAttribute("lastPage", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageCount", count);
        model.addAttribute("isFirstPage", data.isFirst());
        model.addAttribute("isLastPage", data.isLast());
        model.addAttribute("searchForm", new Search(search));
        model.addAttribute("search", search);
        model.addAttribute("searchQuery", "s=" + search);
        model.addAttribute("from", toDate(from));
        model.addAttribute("to", toDate(to));
        return "roles.html";
    }
    
    public String redirect(Model model,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate,
            @RequestParam(required = false, name = "s") String search) {

        page = validatePage(page);
        count = validateCount(count);
        LocalDateTime to = toLocalDateTime(toDate);
        LocalDateTime from = toLocalDateTime(fromDate);
        search = validateSearch(search);

        to = to == null ? LocalDateTime.now() : to;
        from = from == null ? to.minusYears(1) : from;

        Page<Role> data = authorityService.getAllRoles(search,from, to, page, count);

        int totalPages = data.getTotalPages();
        int currentPage = data.getNumber() + 1;
        List<Role> roles = data.getContent();
        
        List<String> grants = new ArrayList<>();
        authorityService.getAllPermission().forEach(p -> {
            grants.add(p.toUpperCase()+"_READ");
            grants.add(p.toUpperCase()+"_WRITE");
        });
        

        List<Pagination> pagination = IntStream.range(1, totalPages + 1)
                .mapToObj(pageNo -> new Pagination(pageNo, pageNo == currentPage))
                .collect(Collectors.toList());

        model.addAttribute("roleForm", new Role());
        model.addAttribute("roles", roles);
        model.addAttribute("grants", grants);
        model.addAttribute("pagination", pagination);
        model.addAttribute("lastPage", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageCount", count);
        model.addAttribute("isFirstPage", data.isFirst());
        model.addAttribute("isLastPage", data.isLast());
        model.addAttribute("searchForm", new Search(search));
        model.addAttribute("search", search);
        model.addAttribute("searchQuery", "s=" + search);
        model.addAttribute("from", toDate(from));
        model.addAttribute("to", toDate(to));
        return "redirect:/roles";
    }

    
    

    

//    public String post(
//            Model model,
//            @ModelAttribute String role,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer count,
//            @RequestParam(required = false, name = "f") String fromDate,
//            @RequestParam(required = false, name = "t") String toDate,
//            @RequestParam(required = false, name = "s") String search) throws ServiceException {
//        String rolename = role.getRolename();
//        String password = role.getPassword();
//        String firstname = role.getPrincipals().get("firstname");
//        String lastname = role.getPrincipals().get("lastname");
//        String email = role.getPrincipals().get("email");
//        String phone = role.getPrincipals().get("phone");
//        List<String> roles = role.getRoles();
//
//        RoleEntity createdRole = roleService.createRole(rolename, password, firstname, lastname, email, phone, roles);
//        return redirect(model, page, count, fromDate, toDate, search);
//    }
    
    @PostMapping("/create")
    public String post(
            Model model,
            @ModelAttribute Role role,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate,
            @RequestParam(required = false, name = "s") String search) throws ServiceException, Exception {
        RoleEntity createdRole = authorityService.createRole(role);
        return list(model, page, count, fromDate, toDate, search);
    }

    @GetMapping("/{value}/delete")
    public ResponseEntity delete(Model model, @PathVariable String value,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate,
            @RequestParam(required = false, name = "s") String search
    ) throws Exception {
        if (value.endsWith("*")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("You cannot delete system role"));
        } else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(authorityService.deleteRole(value));
        }
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
