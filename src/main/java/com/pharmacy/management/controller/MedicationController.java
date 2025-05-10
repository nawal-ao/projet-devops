package com.pharmacy.management.controller;

import com.pharmacy.management.model.Medication;
import com.pharmacy.management.model.StockTransaction;
import com.pharmacy.management.service.MedicationService;
import com.pharmacy.management.service.StockTransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;
    
    @Autowired
    private StockTransactionService transactionService;

    @GetMapping
    public String listMedications(Model model) {
        List<Medication> medications = medicationService.getAllMedications();
        model.addAttribute("medications", medications);
        return "medications/list";
    }
    
    @GetMapping("/new")
    public String newMedicationForm(Model model) {
        model.addAttribute("medication", new Medication());
        return "medications/form";
    }
    
    @PostMapping
    public String saveMedication(@Valid @ModelAttribute Medication medication, 
                                BindingResult result, 
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "medications/form";
        }
        
        try {
            medicationService.saveMedication(medication);
            redirectAttributes.addFlashAttribute("success", "Médicament enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        
        return "redirect:/medications";
    }
    
    @GetMapping("/{id}/edit")
    public String editMedicationForm(@PathVariable Long id, Model model) {
        Medication medication = medicationService.getMedicationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament non trouvé"));
        model.addAttribute("medication", medication);
        return "medications/form";
    }
    
    @GetMapping("/{id}")
    public String viewMedication(@PathVariable Long id, Model model) {
        Medication medication = medicationService.getMedicationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament non trouvé"));
        model.addAttribute("medication", medication);
        
        List<StockTransaction> transactions = transactionService.getTransactionsByMedicationId(id);
        model.addAttribute("transactions", transactions);
        
        return "medications/view";
    }
    
    @GetMapping("/{id}/stock")
    public String stockForm(@PathVariable Long id, Model model) {
        Medication medication = medicationService.getMedicationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médicament non trouvé"));
        model.addAttribute("medication", medication);
        model.addAttribute("transaction", new StockTransaction());
        return "medications/stock";
    }
    
    @PostMapping("/{id}/stock")
    public String updateStock(@PathVariable Long id, 
                             @RequestParam StockTransaction.TransactionType type,
                             @RequestParam int quantity,
                             @RequestParam(required = false) String notes,
                             RedirectAttributes redirectAttributes) {
        try {
            medicationService.updateStock(id, quantity, type, notes, null);
            redirectAttributes.addFlashAttribute("success", "Stock mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
        }
        
        return "redirect:/medications/" + id;
    }
    
    @GetMapping("/low-stock")
    public String lowStockMedications(Model model) {
        List<Medication> medications = medicationService.getLowStockMedications();
        model.addAttribute("medications", medications);
        model.addAttribute("title", "Médicaments en stock bas");
        return "medications/filtered-list";
    }
    
    @GetMapping("/expiring")
    public String expiringMedications(Model model) {
        List<Medication> medications = medicationService.getExpiringMedications();
        model.addAttribute("medications", medications);
        model.addAttribute("title", "Médicaments proches de l'expiration");
        return "medications/filtered-list";
    }
    
    @GetMapping("/expired")
    public String expiredMedications(Model model) {
        List<Medication> medications = medicationService.getExpiredMedications();
        model.addAttribute("medications", medications);
        model.addAttribute("title", "Médicaments expirés");
        return "medications/filtered-list";
    }
    
    @GetMapping("/scan")
    public String scanBarcode() {
        return "medications/scan";
    }
    
    @GetMapping("/search")
    public String searchByBarcode(@RequestParam String barcode, RedirectAttributes redirectAttributes) {
        return medicationService.getMedicationByBarcode(barcode)
                .map(medication -> "redirect:/medications/" + medication.getId())
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Médicament non trouvé avec le code barre: " + barcode);
                    return "redirect:/medications";
                });
    }
}
