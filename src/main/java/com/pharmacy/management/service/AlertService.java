package com.pharmacy.management.service;

import com.pharmacy.management.model.Alert;
import com.pharmacy.management.model.Medication;
import com.pharmacy.management.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private MedicationRepository medicationRepository;

    public List<Alert> getAllActiveAlerts() {
        return alertRepository.findByResolvedOrderByCreatedAtDesc(false);
    }
    
    public List<Alert> getResolvedAlerts() {
        return alertRepository.findByResolvedOrderByCreatedAtDesc(true);
    }
    
    @Transactional
    public Alert createAlert(Medication medication, Alert.AlertType type, String message) {
        Alert alert = new Alert();
        alert.setMedication(medication);
        alert.setType(type);
        alert.setMessage(message);
        return alertRepository.save(alert);
    }
    
    @Transactional
    public Alert createAlertIfNotExists(Medication medication, Alert.AlertType type, String message) {
        List<Alert> existingAlerts = alertRepository.findByMedicationAndTypeAndResolved(medication, type, false);
        if (existingAlerts.isEmpty()) {
            return createAlert(medication, type, message);
        }
        return existingAlerts.get(0);
    }
    
    @Transactional
    public Alert resolveAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alerte non trouvée"));
        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }
    
    @Transactional
    public void resolveAlerts(Medication medication) {
        List<Alert> alerts = alertRepository.findByMedicationAndResolved(medication, false);
        for (Alert alert : alerts) {
            alert.setResolved(true);
            alert.setResolvedAt(LocalDateTime.now());
            alertRepository.save(alert);
        }
    }
    
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void checkAndCreateAlerts() {
        // Check for low stock medications
        List<Medication> lowStockMedications = medicationRepository.findLowStockMedications();
        for (Medication medication : lowStockMedications) {
            createAlertIfNotExists(
                medication, 
                Alert.AlertType.LOW_STOCK, 
                "Stock bas pour " + medication.getName() + ": " + medication.getCurrentStock() + " unités (minimum: " + medication.getMinimumStock() + ")"
            );
        }
        
        // Check for expiring medications
        List<Medication> expiringMedications = medicationRepository.findExpiringMedications(java.time.LocalDate.now().plusMonths(3));
        for (Medication medication : expiringMedications) {
            if (!medication.isExpired()) {
                createAlertIfNotExists(
                    medication, 
                    Alert.AlertType.EXPIRING_SOON, 
                    "Expiration proche pour " + medication.getName() + ": " + medication.getExpirationDate()
                );
            }
        }
        
        // Check for expired medications
        List<Medication> expiredMedications = medicationRepository.findExpiredMedications();
        for (Medication medication : expiredMedications) {
            createAlertIfNotExists(
                medication, 
                Alert.AlertType.EXPIRED, 
                "Médicament expiré: " + medication.getName() + " (expiré le " + medication.getExpirationDate() + ")"
            );
        }
    }
}
