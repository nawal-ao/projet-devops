package com.pharmacy.management.controller;

import com.pharmacy.management.model.Alert;
import com.pharmacy.management.model.Medication;
import com.pharmacy.management.service.AlertService;
import com.pharmacy.management.service.MedicationService;
import com.pharmacy.management.service.StockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private MedicationService medicationService;
    
    @Autowired
    private AlertService alertService;
    
    @Autowired
    private StockTransactionService transactionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get low stock medications
        List<Medication> lowStockMedications = medicationService.getLowStockMedications();
        model.addAttribute("lowStockCount", lowStockMedications.size());
        
        // Get expiring medications
        List<Medication> expiringMedications = medicationService.getExpiringMedications();
        model.addAttribute("expiringCount", expiringMedications.size());
        
        // Get expired medications
        List<Medication> expiredMedications = medicationService.getExpiredMedications();
        model.addAttribute("expiredCount", expiredMedications.size());
        
        // Get active alerts
        List<Alert> activeAlerts = alertService.getAllActiveAlerts();
        model.addAttribute("alertCount", activeAlerts.size());
        model.addAttribute("alerts", activeAlerts);
        
        // Get total medication count
        long totalMedications = medicationService.getAllMedications().size();
        model.addAttribute("totalMedications", totalMedications);
        
        return "dashboard";
    }
}
