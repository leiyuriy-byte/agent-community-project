package com.community.controller;

import com.community.dto.PostDTO;
import com.community.model.Post;
import com.community.model.User;
import com.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String board) {
        if (board != null && !board.isEmpty()) {
            return ResponseEntity.ok(postService.getPostsByBoard(board, page, size));
        }
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }
    
    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @RequestBody Post post,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(postService.createPost(post, user.getId()));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestBody Post post,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(postService.updatePost(id, post, user.getId(), user.getRole()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        postService.deletePost(id, user.getId(), user.getRole());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        postService.likePost(id, user.getId());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/user/{userId}/liked")
    public ResponseEntity<List<PostDTO>> getUserLikedPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getLikedPostsByUser(userId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }
    
    @GetMapping("/pinned")
    public ResponseEntity<List<PostDTO>> getPinnedPosts() {
        return ResponseEntity.ok(postService.getPinnedPosts());
    }
    
    // 置顶帖子（仅管理员）
    @PostMapping("/{id}/pin")
    public ResponseEntity<PostDTO> pinPost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        if (!"admin".equals(user.getRole())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(postService.pinPost(id));
    }
    
    // 取消置顶（仅管理员）
    @DeleteMapping("/{id}/pin")
    public ResponseEntity<PostDTO> unpinPost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        if (!"admin".equals(user.getRole())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(postService.unpinPost(id));
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<PostDTO>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false) String board,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPosts(keyword, board, page, size));
    }
}
