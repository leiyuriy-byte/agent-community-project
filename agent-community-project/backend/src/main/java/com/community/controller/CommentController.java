package com.community.controller;

import com.community.model.Comment;
import com.community.model.User;
import com.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
    
    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.addComment(
            postId, user.getId(), request.get("content")));
    }
}
