package com.community.service;

import com.community.dto.CommentDTO;
import com.community.model.Comment;
import com.community.model.User;
import com.community.repository.CommentRepository;
import com.community.repository.PostRepository;
import com.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }
    
    @Transactional
    public Comment addComment(Long postId, Long userId, String content) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("帖子不存在");
        }
        
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        
        return commentRepository.save(comment);
    }
    
    public List<Comment> getCommentsByUser(Long userId) {
        return commentRepository.findByUserId(userId);
    }
    
    public Object toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
        });
        
        return dto;
    }
}
