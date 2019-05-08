/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.services;

import com.demo.components.EmailSender;
import com.demo.dtos.Address;
import com.demo.dtos.Email;
import com.demo.dtos.UserDto;
import com.demo.entities.UserEntity;
import com.demo.enums.EmailType;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 *
 * @author obinna.asuzu
 */
@Service
public class EmailService {

    @Autowired
    public EmailSender emailSender;
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private ServletContext servletContext;

    

    public void sendWelcomeMessage(Address address, UserEntity user, String token) throws Exception {
        WebContext ctx = new WebContext(request, response, servletContext);
        String url = getBaseUrl() +"/users/"+user.getUsername()+"/activate?token="+token;
        ctx.setVariable("user", new UserDto(user));
        ctx.setVariable("baseUrl", url);

        String contextPath = request.getContextPath();
        StringBuffer requestURL = request.getRequestURL();

        System.out.println("contextPath: " + contextPath);
        System.out.println("requestURL: " + requestURL);

        String html = this.templateEngine.process("welcome-email.html", ctx);

        Email email = new Email();
        email.getTo().add(address);
        email.setSubject("Welcome to Demo");
        email.setType(EmailType.HTML);
        email.setBody(html);

        emailSender.send(email);
    }

    public String getBaseUrl() {
        if ((request.getServerPort() == 80)
                || (request.getServerPort() == 443)) {
            return request.getScheme() + "://"
                    + request.getServerName()
                    + request.getContextPath();
        } else {
            return request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath();
        }
    }

}
