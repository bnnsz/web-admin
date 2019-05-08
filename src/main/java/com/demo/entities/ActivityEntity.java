/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.entities;

import com.demo.enums.ActivityType;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author ObinnaAsuzu
 */
@Entity
public class ActivityEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name="LOG_TIMESTAMP")
    private LocalDateTime timestamp;
    
    @Column
    private String subject;
    
    @Column(name="LOG_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private ActivityType verb;
    
    @Column
    private String objectDescription;
    
    @Column
    private String objectRef;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
     * @return the verb
     */
    public ActivityType getVerb() {
        return verb;
    }

    /**
     * @param verb the verb to set
     */
    public void setVerb(ActivityType verb) {
        this.verb = verb;
    }

    

    /**
     * @return the objectDescription
     */
    public String getObjectDescription() {
        return objectDescription;
    }

    /**
     * @param objectDescription the objectDescription to set
     */
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    /**
     * @return the objectRef
     */
    public String getObjectRef() {
        return objectRef;
    }

    /**
     * @param objectRef the objectRef to set
     */
    public void setObjectRef(String objectRef) {
        this.objectRef = objectRef;
    }

    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActivityEntity)) {
            return false;
        }
        ActivityEntity other = (ActivityEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.demo.security.entities.ActivityEntity[ id=" + id + " ]";
    }
    
}
