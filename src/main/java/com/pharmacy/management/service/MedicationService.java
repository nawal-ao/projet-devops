package com.pharmacy.management.service;

import com.pharmacy.management.model.Alert;
import com.pharmacy.management.model.Medication;
import com.pharmacy.management.model.StockTransaction;
import com.pharmacy.management.repository.AlertRepository;
import com.pharmacy.management.repository.MedicationRepository;
import com.pharmacy.management.repository.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;
    
    @Autowired
    private StockTransactionRepository transactionRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private AlertService alertService;

    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }
    
    public Optional<Medication> getMedicationById(Long id) {
        return medicationRepository.findById(id);
    }
    
    public Optional<Medication> getMedicationByBarcode(String barcode) {
        return medicationRepository.findByBarcode(barcode);
    }
    
    @Transactional
    public Medication saveMedication(Medication medication) {
        Medication savedMedication = medicationRepository.save(medication);
        checkAndCreateAlerts(savedMedication);
        return savedMedication;
    }
    
    @Transactional
    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }
    
    @Transactional
    public void updateStock(Long medicationId, int quantity, StockTransaction.TransactionType type, String notes, Long userId) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new IllegalArgumentException("Médicament non trouvé"));
        
        StockTransaction transaction = new StockTransaction();
        transaction.setMedication(medication);
        transaction.setQuantity(quantity);
        transaction.setType(type);
        transaction.setNotes(notes);
        
        // Set user if provided
        if (userId != null) {
            // Assuming you have a user service to get the user
            // transaction.setUser(userService.getUserById(userId));
        }
        
        transactionRepository.save(transaction);
        
        // Update medication stock
        if (type == StockTransaction.TransactionType.STOCK_IN) {
            medication.setCurrentStock(medication.getCurrentStock() + quantity);
        } else if (type == StockTransaction.TransactionType.STOCK_OUT) {
            if (medication.getCurrentStock() < quantity) {
                throw new IllegalArgumentException("Stock insuffisant");
            }
            medication.setCurrentStock(medication.getCurrentStock() - quantity);
        } else if (type == StockTransaction.TransactionType.ADJUSTMENT) {
            medication.setCurrentStock(quantity);
        } else if (type == StockTransaction.TransactionType.EXPIRED) {
            if (medication.getCurrentStock() < quantity) {
                throw new IllegalArgumentException("Stock insuffisant");
            }
            medication.setCurrentStock(medication.getCurrentStock() - quantity);
        }
        
        medicationRepository.save(medication);
        checkAndCreateAlerts(medication);
    }
    
    public List<Medication> getLowStockMedications() {
        return medicationRepository.findLowStockMedications();
    }
    
    public List<Medication> getExpiringMedications() {
        LocalDate threeMonthsFromNow = LocalDate.now().plusMonths(3);
        return medicationRepository.findExpiringMedications(threeMonthsFromNow);
    }
    
    public List<Medication> getExpiredMedications() {
        return medicationRepository.findExpiredMedications();
    }
    
    private void checkAndCreateAlerts(Medication medication) {
        // Check for low stock
        if (medication.isLowStock()) {
            alertService.createAlertIfNotExists(
                medication, 
                Alert.AlertType.LOW_STOCK, 
                "Stock bas pour " + medication.getName() + ": " + medication.getCurrentStock() + " unités (minimum: " + medication.getMinimumStock() + ")"
            );
        }
        
        // Check for expiring soon
        if (medication.isExpiringSoon() && !medication.isExpired()) {
            alertService.createAlertIfNotExists(
                medication, 
                Alert.AlertType.EXPIRING_SOON, 
                "Expiration proche pour " + medication.getName() + ": " + medication.getExpirationDate()
            );
        }
        
        // Check for expired
        if (medication.isExpired()) {
            alertService.createAlertIfNotExists(
                medication, 
                Alert.AlertType.EXPIRED, 
                "Médicament expiré: " + medication.getName() + " (expiré le " + medication.getExpirationDate() + ")"
            );
        }
    }
}
