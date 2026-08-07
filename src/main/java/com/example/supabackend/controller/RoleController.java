package com.example.supabackend.controller;

import com.example.supabackend.model.Role;
import com.example.supabackend.model.User;
import com.example.supabackend.repository.RoleRepository;
import com.example.supabackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class RoleController {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;

    public RoleController(RoleRepository roleRepo, UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Role> listRoles() {
        return roleRepo.findAll();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRole(@RequestBody Role r) {
        if (roleRepo.existsByName(r.getName())) return ResponseEntity.badRequest().body("Role exists");
        roleRepo.save(r);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/users/{username}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRole(@PathVariable String username, @RequestBody Role role) {
        Optional<User> ou = userRepo.findByUsername(username);
        if (ou.isEmpty()) return ResponseEntity.notFound().build();
        Role r = roleRepo.findByName(role.getName()).orElseGet(() -> roleRepo.save(role));
        User u = ou.get();
        u.getRoles().add(r);
        userRepo.save(u);
        return ResponseEntity.ok().build();
    }
}
