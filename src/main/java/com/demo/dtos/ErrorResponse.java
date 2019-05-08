/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

import com.demo.exceptions.ServiceException;
import com.demo.enums.Error;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author obinna.asuzu
 */
public class ErrorResponse {
    private String date;
    private String message;
    private int code;
    private Error serverError;

    public ErrorResponse(String message) {
        this.date = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss a").format(new Date());
        this.message = message;
    }

    public ErrorResponse(Throwable ex) {
        this.date = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss a").format(new Date());
        this.message = ex.getMessage();
    }
    
    public ErrorResponse(ServiceException ex) {
        this.date = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss a").format(new Date());
        this.message = ex.getError().getMessage();
        this.code = ex.getError().getCode();
        this.serverError = ex.getError();
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

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the serverError
     */
    public Error getServerError() {
        return serverError;
    }

    /**
     * @param serverError the serverError to set
     */
    public void setServerError(Error serverError) {
        this.serverError = serverError;
    }

    

}
