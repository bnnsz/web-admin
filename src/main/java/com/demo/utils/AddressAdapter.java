/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.utils;

import com.demo.dtos.Address;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author obinna.asuzu
 */
public class AddressAdapter {
    public static InternetAddress toInternetAddress(Address address){
        try {
            return new InternetAddress(address.getValue(), address.getName());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AddressAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
