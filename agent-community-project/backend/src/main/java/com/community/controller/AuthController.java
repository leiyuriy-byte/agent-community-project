package com.community.controller;

import com.community.dto.*;
import com.community.model.User;
import com.community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getUserById(user.getId()));
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @AuthenticationPrincipal User user,
            @RequestBody User userDetails) {
        return ResponseEntity.ok(authService.updateUser(user.getId(), userDetails));
    }
}
