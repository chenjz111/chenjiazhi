package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 实时旅游动态表 - 统一替代原有的CityNotice和Notice
 * 这是整个项目最核心的创新点
 */
@Data
@TableName("tb_travel_notice")
public class TravelNotice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long cityId;            // 关联城市ID
    private Long attractionId;      // 关联景点ID（可选）
    private String city;            // 城市名称（冗余字段，方便查询）
    private String attractionName;  // 景点名称（冗余字段，方便查询）
    private String title;           // 动态标题，如"越秀公园西门暂停开放"
    private String content;         // 动态内容
    private String category;        // 动态分类：景区关闭/交通异常/游客拥堵/临时活动
    private String level;           // 预警级别：info/warning/danger
    private String images;          // 现场照片（多张用逗号分隔）
    private Long publisherId;       // 发布者用户ID
    private String status;          // 状态：PENDING/APPROVED/REJECTED
    private LocalDateTime createTime;
}