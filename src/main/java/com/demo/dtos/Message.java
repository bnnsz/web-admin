/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author obinna.asuzu
 */
@XmlRootElement
public class Message {
    
    
    
    

    public enum MsgType {
        success, info, error, warning
    }
    private String title;
    private String text;
    private MsgType type;
    private String styling = "bootstrap3";

    public Message() {
    }

    private Message(String title, String text, MsgType type) {
        this.title = title;
        this.text = text;
        this.type = type == MsgType.warning ? null : type;
    }

    private Message(String title, String text) {
        this.title = title;
        this.text = text;
    }

    private Message(String text, MsgType type) {
        this.text = text;
        this.type = type;
    }

    private Message(String text) {
        this.text = text;
    }

    public static Message error(String text) {
        return new Message(text, MsgType.error);
    }

    public static Message error(String title, String text) {
        return new Message(title, text, MsgType.error);
    }

    public static Message success(String text) {
        return new Message(text, MsgType.success);
    }

    public static Message success(String title, String text) {
        return new Message(title, text, MsgType.success);
    }

    public static Message info(String text) {
        return new Message(text, MsgType.info);
    }

    public static Message info(String title, String text) {
        return new Message(title, text, MsgType.info);
    }

    public static Message warning(String text) {
        return new Message(text);
    }

    public static Message warning(String title, String text) {
        return new Message(title, text);
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the type
     */
    public MsgType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(MsgType type) {
        this.type = type;
    }

    /**
     * @return the styling
     */
    public String getStyling() {
        return styling;
    }

    /**
     * @param styling the styling to set
     */
    public void setStyling(String styling) {
        this.styling = styling;
    }

}
