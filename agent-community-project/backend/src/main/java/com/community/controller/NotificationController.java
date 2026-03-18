package com.community.controller;

import com.community.model.Notification;
import com.community.model.User;
import com.community.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.ok(Map.of("count", 0L));
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.ok(Map.of("success", false));
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
