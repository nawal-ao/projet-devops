package com.pharmacy.management.repository;

import com.pharmacy.management.model.Alert;
import com.pharmacy.management.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByResolvedOrderByCreatedAtDesc(boolean resolved);
    
    List<Alert> findByMedicationAndTypeAndResolved(Medication medication, Alert.AlertType type, boolean resolved);
    
    List<Alert> findByMedicationAndResolved(Medication medication, boolean resolved);
    
    long countByResolved(boolean resolved);
}
