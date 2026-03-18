package com.community.service;

import com.community.model.User;
import com.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentionService {
    
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    // @提及的正则表达式
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");
    
    /**
     * 解析文本中的@提及
     * @param text 文本内容
     * @return 被提及的用户列表
     */
    public List<User> parseMentions(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        
        Matcher matcher = MENTION_PATTERN.matcher(text);
        Set<String> mentionedUsernames = new HashSet<>();
        
        while (matcher.find()) {
            String username = matcher.group(1);
            mentionedUsernames.add(username.toLowerCase());
        }
        
        // 根据用户名查找用户
        return mentionedUsernames.stream()
            .map(userRepository::findByUsername)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
    
    /**
     * 处理帖子中的@提及通知
     * @param postId 帖子ID
     * @param postTitle 帖子标题
     * @param authorId 作者ID
     * @param content 帖子内容
     */
    public void handlePostMentions(Long postId, String postTitle, Long authorId, String content) {
        List<User> mentionedUsers = parseMentions(content);
        
        for (User mentionedUser : mentionedUsers) {
            // 不通知自己
            if (!mentionedUser.getId().equals(authorId)) {
                notificationService.createNotification(
                    mentionedUser.getId(),
                    "mention",
                    "有人在帖子中提到了你: " + truncateTitle(postTitle),
                    postId
                );
            }
        }
    }
    
    /**
     * 处理评论中的@提及通知
     * @param postId 帖子ID
     * @param commentId 评论ID
     * @param authorId 评论作者ID
     * @param content 评论内容
     * @param postAuthorId 帖子作者ID（用于通知帖子作者）
     */
    public void handleCommentMentions(Long postId, Long commentId, Long authorId, String content, Long postAuthorId) {
        List<User> mentionedUsers = parseMentions(content);
        
        // 使用Set来避免重复通知同一用户
        Set<Long> notifiedUsers = new HashSet<>();
        
        // 通知被@的用户
        for (User mentionedUser : mentionedUsers) {
            if (!mentionedUser.getId().equals(authorId)) { // 不通知自己
                notificationService.createNotification(
                    mentionedUser.getId(),
                    "mention",
                    "有人在评论中提到了你",
                    postId
                );
                notifiedUsers.add(mentionedUser.getId());
            }
        }
        
        // 通知帖子作者（如果没有被@，且不是自己评论）
        if (!authorId.equals(postAuthorId) && !notifiedUsers.contains(postAuthorId)) {
            notificationService.createNotification(
                postAuthorId,
                "comment",
                "有人评论了你的帖子",
                postId
            );
        }
    }
    
    /**
     * 搜索用户（用于@选择器）
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 用户列表
     */
    public List<User> searchUsers(String keyword, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果没有关键词，返回最近活跃的用户
            return userRepository.findAll().stream()
                .filter(User::isEnabled)
                .limit(limit)
                .collect(Collectors.toList());
        }
        return userRepository.searchUsers(keyword.trim()).stream()
            .filter(User::isEnabled)
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    private String truncateTitle(String title) {
        if (title == null) return "";
        return title.length() > 20 ? title.substring(0, 20) + "..." : title;
    }
}
