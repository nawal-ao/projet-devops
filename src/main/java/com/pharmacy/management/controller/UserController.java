package com.pharmacy.management.controller;

import com.pharmacy.management.model.User;
import com.pharmacy.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }
    
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }
    
    @PostMapping
    public String saveUser(@Valid @ModelAttribute User user, 
                          BindingResult result, 
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "users/form";
        }
        
        try {
            if (user.getId() == null) {
                userService.createUser(user);
                redirectAttributes.addFlashAttribute("success", "Utilisateur créé avec succès");
            } else {
                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("success", "Utilisateur mis à jour avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "users/form";
        }
        
        return "redirect:/users";
    }
    
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        model.addAttribute("user", user);
        return "users/form";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
