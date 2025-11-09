/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.transactions.controller;

import com.paymentchain.transactions.business.transactions.TransactionService;
import com.paymentchain.transactions.entities.Transaction;
import com.paymentchain.transactions.respository.TransactionRepository;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author sotobotero
 */@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> findAll() {
        List<Transaction> all = transactionService.findAll();
        return all.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return transactionService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/transactions")
    public ResponseEntity<List<Transaction>> getByIban(@RequestParam String ibanAccount) {
        List<Transaction> list = transactionService.findByIbanOrThrow(ibanAccount);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<Transaction> post(@Valid @RequestBody Transaction input) {
        Transaction saved = transactionService.save(input);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> put(@PathVariable Long id, @Valid @RequestBody Transaction input) {
        Optional<Transaction> maybe = transactionService.findById(id);
        if (maybe.isEmpty()) return ResponseEntity.notFound().build();
        Transaction existing = maybe.get();
        // map fields...
        Transaction updated = transactionService.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteByIdOrThrow(id);
        return ResponseEntity.noContent().build();
    }
}
