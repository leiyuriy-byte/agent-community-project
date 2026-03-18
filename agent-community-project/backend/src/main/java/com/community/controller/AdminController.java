package com.community.controller;

import com.community.dto.*;
import com.community.model.*;
import com.community.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    // 获取统计数据
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepository.count());
        stats.put("postCount", postRepository.count());
        stats.put("commentCount", commentRepository.count());
        stats.put("boardCount", boardRepository.count());
        return ResponseEntity.ok(stats);
    }

    // 获取所有用户
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 禁用/启用用户
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return userRepository.findById(id).map(user -> {
            user.setEnabled("enabled".equals(request.get("status")));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true));
        }).orElse(ResponseEntity.notFound().build());
    }
    
    // 设置用户角色
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return userRepository.findById(id).map(user -> {
            user.setRole(request.get("role"));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true, "role", user.getRole()));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 获取所有帖子（管理）
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPostsForAdmin() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    // 置顶帖子
    @PutMapping("/posts/{id}/pin")
    public ResponseEntity<?> pinPost(@PathVariable Long id) {
        return postRepository.findById(id).map(post -> {
            post.setIsPinned(!post.getIsPinned());
            postRepository.save(post);
            return ResponseEntity.ok(Map.of("success", true, "pinned", post.getIsPinned()));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 删除帖子（管理）
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePostAdmin(@PathVariable Long id) {
        return postRepository.findById(id).map(post -> {
            post.setIsDeleted(true);
            postRepository.save(post);
            return ResponseEntity.ok(Map.of("success", true));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 获取所有评论（管理）
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getAllCommentsForAdmin() {
        return ResponseEntity.ok(commentRepository.findAll());
    }

    // 删除评论（管理）
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteCommentAdmin(@PathVariable Long id) {
        return commentRepository.findById(id).map(comment -> {
            commentRepository.delete(comment);
            return ResponseEntity.ok(Map.of("success", true));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 创建板块
    @PostMapping("/boards")
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        Board saved = boardRepository.save(board);
        return ResponseEntity.ok(saved);
    }

    // 删除板块
    @DeleteMapping("/boards/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
        if (boardRepository.existsById(id)) {
            boardRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.notFound().build();
    }

    // 获取热门帖子
    @GetMapping("/hot-posts")
    public ResponseEntity<List<Post>> getHotPosts() {
        List<Post> hotPosts = postRepository.findTop5ByIsDeletedFalseOrderByLikesCountDesc();
        return ResponseEntity.ok(hotPosts);
    }
}
