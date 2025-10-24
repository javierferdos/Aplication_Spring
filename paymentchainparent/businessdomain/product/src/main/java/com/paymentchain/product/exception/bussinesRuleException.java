/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.product.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 *
 * @author javie
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class bussinesRuleException extends RuntimeException {
    
  private final String code;
    private final HttpStatus httpStatus;
    private final Long id;

    public bussinesRuleException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.id = null;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public bussinesRuleException(Long id, String code, String message, HttpStatus httpStatus) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
    
}
