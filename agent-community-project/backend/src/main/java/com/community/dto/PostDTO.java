package com.community.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String title;
    private String content;
    private String board;
    private Integer likesCount;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
