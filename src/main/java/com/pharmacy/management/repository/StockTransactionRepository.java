package com.pharmacy.management.repository;

import com.pharmacy.management.model.Medication;
import com.pharmacy.management.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    
    List<StockTransaction> findByMedicationOrderByTransactionDateDesc(Medication medication);
    
    List<StockTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM StockTransaction t WHERE t.medication.id = ?1 ORDER BY t.transactionDate DESC")
    List<StockTransaction> findTransactionsByMedicationId(Long medicationId);
    
    @Query("SELECT SUM(CASE WHEN t.type = 'STOCK_IN' THEN t.quantity ELSE -t.quantity END) FROM StockTransaction t WHERE t.medication.id = ?1")
    Integer calculateCurrentStock(Long medicationId);
}
