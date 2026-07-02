package com.travelshare.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long id;
    private String targetType;
    private Long targetId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createTime;
}
