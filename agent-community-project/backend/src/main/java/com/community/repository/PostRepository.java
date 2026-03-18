package com.community.repository;

import com.community.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByIsDeletedFalse(Pageable pageable);
    Page<Post> findByBoardAndIsDeletedFalse(String board, Pageable pageable);
    List<Post> findByUserIdAndIsDeletedFalse(Long userId);
    
    @Query("SELECT p FROM Post p WHERE p.isPinned = true AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findPinnedPosts();
    
    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount + 1 WHERE p.id = :postId")
    void incrementLikes(@Param("postId") Long postId);
    
    List<Post> findTop5ByIsDeletedFalseOrderByLikesCountDesc();
    
    // Search posts by title or content
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);
    
    // Search posts within a specific board
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.board = :board AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPostsInBoard(@Param("keyword") String keyword, @Param("board") String board, Pageable pageable);
}
