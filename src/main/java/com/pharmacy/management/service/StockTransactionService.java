package com.pharmacy.management.service;

import com.pharmacy.management.model.Medication;
import com.pharmacy.management.model.StockTransaction;
import com.pharmacy.management.repository.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockTransactionService {

    @Autowired
    private StockTransactionRepository transactionRepository;

    public List<StockTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<StockTransaction> getTransactionsByMedication(Medication medication) {
        return transactionRepository.findByMedicationOrderByTransactionDateDesc(medication);
    }
    
    public List<StockTransaction> getTransactionsByMedicationId(Long medicationId) {
        return transactionRepository.findTransactionsByMedicationId(medicationId);
    }
    
    public List<StockTransaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByTransactionDateBetween(start, end);
    }
    
    public StockTransaction saveTransaction(StockTransaction transaction) {
        return transactionRepository.save(transaction);
    }
}
