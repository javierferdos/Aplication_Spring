/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.billing.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;



/**
 *
 * @author sotobotero
 */
@Entity
@Data
@Schema(name="invoice", description = "Model represent a invoice on database")
public class Invoice {
   @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
   private long id;
   @Schema(name = "CustomerId", required = true, example = "2", defaultValue = "1", description = "Unique id of customer that represent ")
   private long customerId;
   
   @Schema(name = "number", required = true, example = "2", defaultValue = "1", description = "Number given on fisical invoice")
   private String number;
   
   private String detail;
   
   private double amount;
   
}
