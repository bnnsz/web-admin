/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

import com.demo.entities.ActivityEntity;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.ocpsoft.prettytime.PrettyTime;

/**
 *
 * @author obinna.asuzu
 */
public class NarratedActivity {

    public static NarratedActivity create(ActivityEntity entity) {
        return new NarratedActivity(entity, false);
    }

    public static NarratedActivity createVerbose(ActivityEntity entity) {
        return new NarratedActivity(entity, true);
    }

    private String subject;
    private String narration;
    private String date;

    public NarratedActivity() {
    }

    private NarratedActivity(ActivityEntity entity, boolean verbose) {
        subject = entity.getSubject();
        date = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a").format(entity.getTimestamp());
        StringBuilder builder = new StringBuilder();
        if (verbose) {
            builder.append(entity.getSubject());
            builder.append(" ");
        }

        builder.append(entity.getVerb().getValue());
        if (entity.getObjectDescription() != null) {
            builder.append(" ");
            builder.append(entity.getObjectDescription());
        }

        builder.append(" ");
        builder.append(new PrettyTime().format(Date
                .from(entity.getTimestamp().atZone(ZoneId.systemDefault()).toInstant())));
        narration = builder.toString();
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the narration
     */
    public String getNarration() {
        return narration;
    }

    /**
     * @param narration the narration to set
     */
    public void setNarration(String narration) {
        this.narration = narration;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

}
