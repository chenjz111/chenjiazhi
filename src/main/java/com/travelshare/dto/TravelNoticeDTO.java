package com.travelshare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TravelNoticeDTO {
    @NotBlank(message = "城市不能为空")
    private String city;
    private String attractionName; // 可选
    private Long attractionId;     // 可选

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String category;       // 动态分类
    private String level;          // 预警级别
    private String images;         // 图片URL
    private Long publisherId;
}