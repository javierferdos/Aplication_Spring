/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.transactions.business.transactions;

import com.paymentchain.transactions.entities.Transaction;
import com.paymentchain.transactions.exception.bussinesRuleException;
import com.paymentchain.transactions.respository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author javie
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    
    
    
    private static final double FEE_RATE = 0.0098; // 0.98%
    

    public List<Transaction> findAll() {
        List<Transaction> list = transactionRepository.findAll();
        // preferible devolver lista vacía y que controller decida 204/200
        return list;
    }

    public Transaction findByIdOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new bussinesRuleException("TX404", "Transaction not found with id " + id, HttpStatus.NOT_FOUND));
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> findByIbanOrThrow(String iban) {
        List<Transaction> list = transactionRepository.findByIbanAccount(iban);
        if (list == null || list.isEmpty()) {
            throw new bussinesRuleException("TX_IBAN404", "No transactions found for IBAN " + iban, HttpStatus.NOT_FOUND);
        }
        return list;
    }

    /*public Transaction save(Transaction transaction) {
        if (transaction.getAmount() < 0) {
            throw new bussinesRuleException("TX400", "Transaction amount cannot be negative", HttpStatus.BAD_REQUEST);
        }
        Transaction saved = transactionRepository.save(transaction);
        log.info("Saved transaction id={} amount={}", saved.getId(), saved.getAmount());
        return saved;
    }*/
    
    @Transactional
    public Transaction save(Transaction transaction) {
        if (transaction == null) {
            throw new bussinesRuleException("TX400", "Transaction payload is required", HttpStatus.BAD_REQUEST);
        }

        // Validaciones básicas
        if (transaction.getIbanAccount() == null || transaction.getIbanAccount().isBlank()) {
            throw new bussinesRuleException("TX400", "IbanAccount is required", HttpStatus.PRECONDITION_FAILED);
        }

        // Si amount es null o NaN
        if (Double.isNaN(transaction.getAmount())) {
            throw new bussinesRuleException("TX400", "Invalid amount", HttpStatus.PRECONDITION_FAILED);
        }

        double originalAmount = transaction.getAmount();

        // Si es retiro (amount < 0) calculamos fee y finalAmount
        if (originalAmount < 0) {
            double fee = Math.abs(originalAmount) * FEE_RATE;
            // Redondeo opcional a 2 decimales (moneda) — usa BigDecimal si necesitas precisión exacta
            fee = roundToTwoDecimals(fee);

            double finalAmount = originalAmount - fee;
            finalAmount = roundToTwoDecimals(finalAmount);

            // calcular balance actual antes de aplicar
            double currentBalance = calculateBalance(transaction.getIbanAccount());

            double newBalance = currentBalance + finalAmount; // finalAmount es negativo

            // Regla: no permitir que saldo quede <= 0
            if (newBalance <= 0.0) {
                throw new bussinesRuleException("TX_OVERDRAW", "Insufficient funds: operation would leave account with balance <= 0", HttpStatus.PRECONDITION_FAILED);
            }

            // setear fee y amount finales
            transaction.setFee(fee);
            transaction.setAmount(finalAmount);
        } else {
            // abono (positivo) -> fee normalmente 0 (según regla dada)
            if (transaction.getFee() == 0.0) {
                transaction.setFee(0.0);
            }
            // No se requiere comprobación de saldo para abonos
        }

        Transaction saved = transactionRepository.save(transaction);
        log.info("Saved transaction id={} amount={} fee={} iban={}", saved.getId(), saved.getAmount(), saved.getFee(), saved.getIbanAccount());
        return saved;
    }
    
    
    
    

    @Transactional
    public void deleteByIdOrThrow(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new bussinesRuleException("TX404", "Transaction not found with id " + id, HttpStatus.NOT_FOUND);
        }
        transactionRepository.deleteById(id);
        log.info("Deleted transaction id={}", id);
    }
    
    
    
    public double calculateBalance(String ibanAccount){
    
        List<Transaction> list = transactionRepository.findByIbanAccount(ibanAccount);
        if(list == null || list.isEmpty()){
            return 0.0;
        }
        double sum = 0.0;
        
        for(Transaction t : list){
            sum += t.getAmount();
        }
        return sum;
    }
    
     // Helper: redondeo a 2 decimales
    private double roundToTwoDecimals(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
    
}
