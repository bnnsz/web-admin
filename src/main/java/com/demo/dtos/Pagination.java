/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.dtos;

/**
 *
 * @author obinna.asuzu
 */
public class Pagination {

    private int pageNo;
    private String active;

    public Pagination() {
    }

    public Pagination(Integer pageNo, boolean active) {
        this.pageNo = pageNo;
        this.active = active ? "active" : "";
    }
    
    

    /**
     * @return the pageNo
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo the pageNo to set
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * @return the active
     */
    public String getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(String active) {
        this.active = active;
    }

    
    
    
    
}
