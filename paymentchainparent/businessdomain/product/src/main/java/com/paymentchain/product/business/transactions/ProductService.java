/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.product.business.transactions;

import com.paymentchain.product.entities.Product;
import com.paymentchain.product.exception.bussinesRuleException;
import com.paymentchain.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author javie
 */

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product create(Product p) {
        // regla de negocio: code no nulo y name no vacio
        if (p.getCode() == null || p.getCode().isBlank()) {
            throw new bussinesRuleException("PROD_1001", "Product code is required", HttpStatus.PRECONDITION_FAILED);
        }
        if (p.getName() == null || p.getName().isBlank()) {
            throw new bussinesRuleException("PROD_1002", "Product name is required", HttpStatus.PRECONDITION_FAILED);
        }
        return productRepository.save(p);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product update(Long id, Product input) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new bussinesRuleException("PROD_1003", "Product not found", HttpStatus.NOT_FOUND));
        if (input.getName() != null) existing.setName(input.getName());
        if (input.getCode() != null) existing.setCode(input.getCode());
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new bussinesRuleException("PROD_1003", "Product not found", HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
    }
}