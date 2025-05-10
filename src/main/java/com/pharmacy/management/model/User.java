package com.pharmacy.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @NotBlank(message = "Le nom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le prénom est obligatoire")
    private String lastName;

    @Email(message = "L'email doit être valide")
    @Column(unique = true)
    private String email;

    private boolean active = true;

    private LocalDateTime lastLogin;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (roles.isEmpty()) {
            roles.add("ROLE_USER");
        }
    }
}
