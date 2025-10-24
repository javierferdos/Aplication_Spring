package com.paymentchain.product.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author javie
 */
@Schema(description="description")
@NoArgsConstructor
@Data
public class StandarizedApiExceptionResponse {
    
    @Schema(description="description", name="type", requiredMode=Schema.RequiredMode.REQUIRED, example = "/errors/authentication/not-authorized")
    private String type;
    
    @Schema(description = "description", name ="title", requiredMode = Schema.RequiredMode.REQUIRED, example="The user does not have authotized")
    private String title;
    
    @Schema(description = "description", name="code", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "192" )
    private String code;
    
    @Schema(description = "description", name="detail", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "The user does not have the propertly persmissions to acces the" 
    + "resources, please contact with us https://sotobotero.com")
    private String detail;
    
    @Schema(description = "description", name="instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "/errors/authetication/not-authorized/01")
    private String instance;
    
    public StandarizedApiExceptionResponse(String type, String title, String code, String detail){
        super();
        this.type = type;
        this.title = title;
        this.code = code;
        this.detail = detail;
        
    }
    
    
    
    
}
