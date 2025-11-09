/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author javie
 */
  @Schema(name = "InvoiceResponse", description = "model Represent a invoice on databse")
public class InvoiceResponse {
      
      private Long id;
      
      @Schema(name = "Customer", required = true, example = "2", defaultValue = "1", description = "Unique id of customer that represent ")
   private long customer;
   
   @Schema(name = "number", required = true, example = "2", defaultValue = "1", description = "Number given on fisical invoice")
   private String number;
   
   private String detail;
   
   private double amount;
    
}
