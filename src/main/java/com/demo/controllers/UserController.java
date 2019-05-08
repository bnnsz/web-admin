/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.controllers;

import com.demo.dtos.UserDto;
import com.demo.entities.UserEntity;
import com.demo.dtos.ErrorResponse;
import com.demo.dtos.Pagination;
import com.demo.dtos.Search;
import com.demo.exceptions.ServiceException;
import com.demo.forms.ChangePasswordForm;
import com.demo.forms.UserForm;
import com.demo.services.AuthorityService;
import com.demo.services.UserService;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author obinna.asuzu
 */
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthorityService authorityService;

    @Autowired
    HttpServletRequest request;

    Authentication authentication;

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping()
    public String list(Model model,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false, name = "f") String fromDate,
            @RequestParam(required = false, name = "t") String toDate,
            @RequestParam(required = false, name = "s") String search) {
        LocalDateTime to = toLocalDateTime(toDate);
        LocalDateTime from = toLocalDateTime(fromDate);

        System.out.println("page: " + page);
        System.out.println("count: " + count);
        System.out.println("fromDate: " + from);
        System.out.println("toDate: " + to);
        System.out.println("search: " + search);
        resolveAttributes(page, count, to, from, search);

        page = validatePage(Integer.valueOf(request
                .getSession()
                .getAttribute("userPage.page")
                .toString()));

        count = validateCount(Integer.valueOf(request
                .getSession()
                .getAttribute("userPage.count")
                .toString()));

        to = (LocalDateTime) request.getSession().getAttribute("userPage.to");
        
        from = (LocalDateTime) request.getSession().getAttribute("userPage.from");

        Object searchObj = request.getSession().getAttribute("userPage.search");
        search = validateSearch(searchObj == null ? null : searchObj.toString());

        System.out.println("----------------> userService = null? " + (userService == null));
        System.out.println("page: " + page);
        System.out.println("count: " + count);
        System.out.println("fromDate: " + from);
        System.out.println("toDate: " + to);
        System.out.println("search: " + search);

        Page<UserDto> data = userService.getAllUsers(search, from, to, page, count);

        int totalPages = data.getTotalPages();
        int currentPage = data.getNumber() + 1;
        List<UserDto> users = data.getContent();

        System.out.println("Users size" + users.size());

        List<Pagination> pagination = IntStream.range(1, totalPages + 1)
                .mapToObj(pageNo -> new Pagination(pageNo, pageNo == currentPage))
                .collect(Collectors.toList());

        model.addAttribute("roles", authorityService.getAllRoles());
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("users", users);
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
        return "users.html";
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

        Page<UserDto> data = userService
                .getAllUsers(search, from, to, page, count);

        int totalPages = data.getTotalPages();
        int currentPage = data.getNumber() + 1;
        List<UserDto> users = data.getContent();

        System.out.println("Users size" + users.size());

        List<Pagination> pagination = IntStream.range(1, totalPages + 1)
                .mapToObj(pageNo -> new Pagination(pageNo, pageNo == currentPage))
                .collect(Collectors.toList());

        model.addAttribute("roles", authorityService.getAllRoles());
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("users", users);
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
        return "redirect:/users";
    }

    @GetMapping("/{id}")
    public String get(@PathVariable String id, Model model) throws ServiceException {
        model.addAttribute("user", userService.getUser(resolve(id)));
        return "user.html";
    }

    @GetMapping("/{id}/activate")
    public String activate(@PathVariable String id, @RequestParam String token, Model model, HttpServletRequest req) throws ServiceException, Exception {
        UserEntity user = userService.activateUser(token);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        auth.setDetails("activation");
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("form", new ChangePasswordForm(user.getPassword()));
        return "changepassword.html";
    }

    @PostMapping("/{id}/change-password")
    public String changePassword(@PathVariable String id, ChangePasswordForm form, HttpServletRequest req, Model model) throws ServiceException, Exception {
        boolean changed = userService.changePassword(resolve(id), form.getOldPassword(), form.getNewPassword());
        if (changed) {
            SecurityContextHolder.clearContext();
            req.logout();
            return "redirect:/";
        } else {
            model.addAttribute("username", resolve(id));
            model.addAttribute("form", form);
            return "changepassword.html";
        }
    }

    @GetMapping("/{id}/profile")
    public String getProfile(@PathVariable String id, Model model) throws ServiceException {
        model.addAttribute("profile", userService.getUserProfile(resolve(id)));
        return "user-details.html";
    }

    @PutMapping("/{id}/update")
    public ResponseEntity put(@PathVariable String id, @RequestBody Map<String, String> principles) throws ServiceException {
        UserEntity user = userService.updateUserProfile(resolve(id), principles);
        return ResponseEntity.accepted().body(new UserDto(user));
    }

//    public String post(
//            Model model,
//            @ModelAttribute UserDto user,
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer count,
//            @RequestParam(required = false, name = "f") String fromDate,
//            @RequestParam(required = false, name = "t") String toDate,
//            @RequestParam(required = false, name = "s") String search) throws ServiceException {
//        String username = user.getUsername();
//        String password = user.getPassword();
//        String firstname = user.getPrincipals().get("firstname");
//        String lastname = user.getPrincipals().get("lastname");
//        String email = user.getPrincipals().get("email");
//        String phone = user.getPrincipals().get("phone");
//        List<String> roles = user.getRoles();
//
//        UserEntity createdUser = userService.createUser(username, password, firstname, lastname, email, phone, roles);
//        return redirect(model, page, count, fromDate, toDate, search);
//    }
    @PostMapping("/create")
    public String post(Model model, @ModelAttribute UserForm user) throws ServiceException, Exception {
        String username = user.getUsername();
        String firstname = user.getFirstname();
        String lastname = user.getLastname();
        String email = user.getEmail();
        String phone = user.getPhone();
        List<String> roles = user.getRoles();

        System.out.println(new Gson().toJson(user));

        UserEntity createdUser = userService.registerUser(username, firstname, lastname, email, phone, roles);
        return list(model, null, null, null, null, null);
    }

    @GetMapping("/{id}/delete")
    public ResponseEntity delete(Model model, @PathVariable String id) throws Exception {
        if (id.equalsIgnoreCase("me")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("You cannot delete your account"));
        } else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.deactivateUser(resolve(id)));
        }
    }

    private String resolve(String id) {
        if (id.equalsIgnoreCase("me")) {
            authentication = authentication == null
                    ? SecurityContextHolder.getContext().getAuthentication()
                    : authentication;
            return authentication.getName();
        }
        return id;
    }

    private boolean userHasAuthority(String authority) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch((grantedAuthority) -> authority.equals(grantedAuthority.getAuthority()));
    }

    private UserDto toDto(UserEntity u) {
        return new UserDto(u.getUsername());
    }

    private Integer validateCount(Integer count) {
        count = count == null ? 10 : count;
        return count;
    }

    private Integer validatePage(Integer page) {
        return page == null ? 0 : page - 1;
    }

    private String validateSearch(String search) {
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

    public void resolveAttributes(
            Integer page,
            Integer count,
            LocalDateTime to,
            LocalDateTime from,
            String search) {

        System.out.println("Request = null ? " + (request == null));

        System.out.println("Session = null ? " + (request.getSession() == null));
        if (page != null) {
            request.getSession().setAttribute("userPage.page", page);
        }
        if (count != null) {
            request.getSession().setAttribute("userPage.count", count);
        }
        if (to != null) {
            request.getSession().setAttribute("userPage.to", to);
        }
        if (from != null) {
            request.getSession().setAttribute("userPage.from", from);
        }
        if (search != null) {
            request.getSession().setAttribute("userPage.search", search);
        }

        if (request.getSession().getAttribute("userPage.page") == null) {
            request.getSession().setAttribute("userPage.page", 1);
        }

        if (request.getSession().getAttribute("userPage.count") == null) {
            request.getSession().setAttribute("userPage.count", 10);
        }

        if (request.getSession().getAttribute("userPage.to") == null) {
            request.getSession().setAttribute("userPage.to", LocalDateTime.now());
        }

        if (request.getSession().getAttribute("userPage.from") == null) {
            Object toObj = request.getSession().getAttribute("userPage.to");
            request.getSession().setAttribute("userPage.from", ((LocalDateTime) toObj).minusYears(1));
        }
    }
}
