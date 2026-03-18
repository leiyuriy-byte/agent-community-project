package com.community.controller;

import com.community.dto.UserDTO;
import com.community.model.User;
import com.community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final AuthService authService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {
        return ResponseEntity.ok(authService.updateUser(id, userDetails));
    }
}
