package com.pharmacy.management.repository;

import com.pharmacy.management.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    
    Optional<Medication> findByBarcode(String barcode);
    
    List<Medication> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT m FROM Medication m WHERE m.currentStock <= m.minimumStock AND m.active = true")
    List<Medication> findLowStockMedications();
    
    @Query("SELECT m FROM Medication m WHERE m.expirationDate <= ?1 AND m.active = true")
    List<Medication> findExpiringMedications(LocalDate expirationThreshold);
    
    @Query("SELECT m FROM Medication m WHERE m.expirationDate < CURRENT_DATE AND m.active = true")
    List<Medication> findExpiredMedications();
    
    List<Medication> findByCategory(String category);
}
