package com.pharmacy.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private String description;

    @NotBlank(message = "Le code barre est obligatoire")
    @Column(unique = true)
    private String barcode;

    @NotNull(message = "La date d'expiration est obligatoire")
    private LocalDate expirationDate;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private BigDecimal price;

    @NotNull(message = "Le stock minimum est obligatoire")
    @Positive(message = "Le stock minimum doit être positif")
    private Integer minimumStock;

    @NotNull(message = "Le stock actuel est obligatoire")
    @Positive(message = "Le stock actuel doit être positif")
    private Integer currentStock;

    private String manufacturer;
    
    private String category;
    
    private String location;
    
    private boolean active = true;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
    
    public boolean isLowStock() {
        return currentStock <= minimumStock;
    }
    
    public boolean isExpiringSoon() {
        LocalDate warningDate = LocalDate.now().plusMonths(3);
        return expirationDate.isBefore(warningDate);
    }
    
    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }
}
