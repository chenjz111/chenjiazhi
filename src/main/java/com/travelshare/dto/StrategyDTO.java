package com.travelshare.dto;

import lombok.Data;

@Data
public class StrategyDTO {

    private String title;

    private String content;

    private String city;

    private Long authorId;

    private String images;         // 图片URL(多张逗号分隔)
}