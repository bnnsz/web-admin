/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

import com.demo.entities.GrantedPrivilegeEntity;

/**
 *
 * @author obinna.asuzu
 */
public class Grantedprivilege {

    private String value;
    private boolean read;
    private boolean write;

    public Grantedprivilege() {
    }

    public Grantedprivilege(GrantedPrivilegeEntity entity) {
        value = entity.getValue();
        read = entity.isRead();
        write = entity.isWrite();
    }

    public Grantedprivilege(String value, boolean read, boolean write) {
        this.value = value;
        this.read = read;
        this.write = write;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the read
     */
    public boolean isRead() {
        return read;
    }

    /**
     * @param read the read to set
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * @return the write
     */
    public boolean isWrite() {
        return write;
    }

    /**
     * @param write the write to set
     */
    public void setWrite(boolean write) {
        this.write = write;
    }

}
