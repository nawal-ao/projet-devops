package com.pharmacy.management.controller;

import com.pharmacy.management.model.Medication;
import com.pharmacy.management.model.StockTransaction;
import com.pharmacy.management.service.MedicationService;
import com.pharmacy.management.service.StockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private MedicationService medicationService;
    
    @Autowired
    private StockTransactionService transactionService;

    @GetMapping
    public String reportsDashboard() {
        return "reports/dashboard";
    }
    
    @GetMapping("/stock")
    public String stockReport(Model model) {
        List<Medication> medications = medicationService.getAllMedications();
        model.addAttribute("medications", medications);
        return "reports/stock";
    }
    
    @GetMapping("/expiration")
    public String expirationReport(Model model) {
        List<Medication> expiringMedications = medicationService.getExpiringMedications();
        List<Medication> expiredMedications = medicationService.getExpiredMedications();
        
        model.addAttribute("expiringMedications", expiringMedications);
        model.addAttribute("expiredMedications", expiredMedications);
        
        return "reports/expiration";
    }
    
    @GetMapping("/transactions")
    public String transactionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<StockTransaction> transactions = transactionService.getTransactionsByDateRange(startDateTime, endDateTime);
        
        model.addAttribute("transactions", transactions);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "reports/transactions";
    }
}
