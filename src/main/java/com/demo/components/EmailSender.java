/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.components;

import com.demo.dtos.Address;
import com.demo.dtos.Attachment;
import com.demo.dtos.Email;
import com.demo.enums.EmailType;
import com.demo.utils.AddressAdapter;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author obinna.asuzu
 */

@Component
public class EmailSender {
    @Autowired
    public JavaMailSender javaMailSender;
    @Async
    public void send(Email email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        for (Address to : email.getTo()) {
            helper.addTo(AddressAdapter.toInternetAddress(to));
        }
        for (Address bcc : email.getBcc()) {
            helper.addBcc(AddressAdapter.toInternetAddress(bcc));
        }
        for (Address cc : email.getCc()) {
            helper.addCc(AddressAdapter.toInternetAddress(cc));
        }

        helper.setSubject(email.getSubject());
        helper.setText(email.getBody(), email.getType() == EmailType.HTML);
        for (Attachment attachment : email.getAttachments()) {
            byte[] content = Base64.decodeBase64(attachment.getContent());
            DataSource dataSource = new ByteArrayDataSource(content, attachment.getMimeType());
            helper.addAttachment(attachment.getName(), dataSource);
        }
        javaMailSender.send(message);
    }
}
