package com.community.service;

import com.community.dto.PostDTO;
import com.community.model.Post;
import com.community.model.PostLike;
import com.community.model.User;
import com.community.repository.PostLikeRepository;
import com.community.repository.PostRepository;
import com.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    
    public Page<PostDTO> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "isPinned", "createdAt"));
        return postRepository.findByIsDeletedFalse(pageable).map(this::toDTO);
    }
    
    public Page<PostDTO> getPostsByBoard(String board, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "isPinned", "createdAt"));
        return postRepository.findByBoardAndIsDeletedFalse(board, pageable).map(this::toDTO);
    }
    
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (post.getIsDeleted()) {
            throw new RuntimeException("帖子不存在");
        }
        return toDTO(post);
    }
    
    @Transactional
    public PostDTO createPost(Post post, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        post.setUserId(userId);
        post.setLikesCount(0);
        post.setIsPinned(false);
        post.setIsDeleted(false);
        
        post = postRepository.save(post);
        return toDTO(post);
    }
    
    @Transactional
    public PostDTO updatePost(Long id, Post postDetails, Long userId, String role) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        if (post.getIsDeleted()) {
            throw new RuntimeException("帖子不存在");
        }
        
        if (!post.getUserId().equals(userId) && !"admin".equals(role)) {
            throw new RuntimeException("无权限修改");
        }
        
        if (postDetails.getTitle() != null) {
            post.setTitle(postDetails.getTitle());
        }
        if (postDetails.getContent() != null) {
            post.setContent(postDetails.getContent());
        }
        if (postDetails.getBoard() != null) {
            post.setBoard(postDetails.getBoard());
        }
        
        post = postRepository.save(post);
        return toDTO(post);
    }
    
    @Transactional
    public void deletePost(Long id, Long userId, String role) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        if (!post.getUserId().equals(userId) && !"admin".equals(role)) {
            throw new RuntimeException("无权限删除");
        }
        
        post.setIsDeleted(true);
        postRepository.save(post);
    }
    
    @Transactional
    public void likePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (post.getIsDeleted()) {
            throw new RuntimeException("帖子不存在");
        }
        
        // 检查是否已经点赞
        if (postLikeRepository.existsByUserIdAndPostId(userId, id)) {
            // 取消点赞
            postLikeRepository.deleteByUserIdAndPostId(userId, id);
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        } else {
            // 添加点赞
            PostLike like = new PostLike();
            like.setUserId(userId);
            like.setPostId(id);
            postLikeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
        }
        postRepository.save(post);
    }
    
    public boolean isLikedByUser(Long postId, Long userId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }
    
    public List<PostDTO> getLikedPostsByUser(Long userId) {
        List<Long> postIds = postLikeRepository.findPostIdsByUserId(userId);
        return postIds.stream()
            .map(postRepository::findById)
            .filter(opt -> opt.isPresent() && !opt.get().getIsDeleted())
            .map(opt -> toDTO(opt.get()))
            .collect(Collectors.toList());
    }
    
    public List<PostDTO> getPostsByUser(Long userId) {
        return postRepository.findByUserIdAndIsDeletedFalse(userId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<PostDTO> getPinnedPosts() {
        return postRepository.findPinnedPosts().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    private PostDTO toDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setBoard(post.getBoard());
        dto.setLikesCount(post.getLikesCount());
        dto.setIsPinned(post.getIsPinned());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        
        userRepository.findById(post.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
        });
        
        return dto;
    }
}
