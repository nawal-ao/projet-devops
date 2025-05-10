package com.pharmacy.management.controller;

import com.pharmacy.management.model.Alert;
import com.pharmacy.management.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping
    public String listAlerts(Model model) {
        List<Alert> activeAlerts = alertService.getAllActiveAlerts();
        model.addAttribute("alerts", activeAlerts);
        return "alerts/list";
    }
    
    @GetMapping("/resolved")
    public String listResolvedAlerts(Model model) {
        List<Alert> resolvedAlerts = alertService.getResolvedAlerts();
        model.addAttribute("alerts", resolvedAlerts);
        model.addAttribute("resolved", true);
        return "alerts/list";
    }
    
    @PostMapping("/{id}/resolve")
    public String resolveAlert(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alertService.resolveAlert(id);
            redirectAttributes.addFlashAttribute("success", "Alerte marquée comme résolue");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/alerts";
    }
}
