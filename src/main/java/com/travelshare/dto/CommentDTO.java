package com.travelshare.dto;

import lombok.Data;

@Data
public class CommentDTO {

    private String targetType;

    private Long targetId;

    private Long userId;

    private String content;
}