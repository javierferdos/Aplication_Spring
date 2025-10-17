/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 *
 * @author javie
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class bussinesRuleException extends Exception{
    
    private Long id;
    private String code;
    private HttpStatus httpStatus;
    
    
    public bussinesRuleException(Long id, String code, String messege, HttpStatus httpStatus){
        super(messege);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
       public bussinesRuleException(String code, String messege, HttpStatus httpStatus){
        super(messege);
        this.code = code;
        this.httpStatus = httpStatus;
    }
       
    public bussinesRuleException(String messege, Throwable cause){
        super(messege, cause);
        
    }
    
    
    
    
}
