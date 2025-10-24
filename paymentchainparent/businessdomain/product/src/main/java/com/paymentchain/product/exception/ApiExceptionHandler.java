/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.product.exception;

import com.paymentchain.product.common.StandarizedApiExceptionResponse;
import java.time.Instant;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;


/**
 *
 * @author javie
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(bussinesRuleException.class)
     public ResponseEntity<?> handleBusinessRuleException(bussinesRuleException ex) {
        StandarizedApiExceptionResponse response = new StandarizedApiExceptionResponse(
                "TBUSSINES",
                "Error de validación",
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }
    
    
    
    
    
    

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<StandarizedApiExceptionResponse> handleWebClient(WebClientResponseException ex, HttpServletRequest req) {
        log.error("Error calling remote service: status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString(), ex);
        StandarizedApiExceptionResponse err = new StandarizedApiExceptionResponse(
                "TECNICO",
                "Error interno",
                "9999",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

   @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<StandarizedApiExceptionResponse> handleValidation(
        MethodArgumentNotValidException ex, HttpServletRequest req) {

    // Construimos el mensaje uniendo todos los errores de campo
    String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Error de validación en los campos enviados");

    // Creamos la respuesta estándar con tu estructura
    StandarizedApiExceptionResponse err = new StandarizedApiExceptionResponse(
            "VALIDATION_ERROR",                // type → tipo de error (puedes usar “TBUSSINES” también)
            "Error de validación de entrada",  // title → título o resumen del error
            "400",                             // code → código de error interno o HTTP
            message                            // detail → lista de errores concatenada
    );

    // Devuelve 400 Bad Request
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
}

    @ExceptionHandler(Exception.class)
public ResponseEntity<StandarizedApiExceptionResponse> handleAll(Exception ex, HttpServletRequest req) {
    // 🔹 Registra el error completo en los logs del servidor (útil para diagnóstico)
    log.error("Unexpected error at {} {}", req.getMethod(), req.getRequestURI(), ex);

    // 🔹 Crea una respuesta estandarizada para devolver al cliente
    StandarizedApiExceptionResponse err = new StandarizedApiExceptionResponse(
        "INTERNAL_ERROR",                   // type → tipo de error (puedes definir tus categorías)
        "Error interno del servidor",       // title → mensaje genérico, no expone detalles técnicos
        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), // code → 500
        ex.getMessage()                     // detail → descripción técnica o mensaje del error
    );

    // 🔹 Devuelve un JSON con código HTTP 500 y el objeto de error
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
}

}