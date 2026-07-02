package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_attraction")
public class Attraction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;            // 景点名称，如"广州塔"
    private Long cityId;            // 所属城市ID
    private String description;     // 景点介绍
    private String openTime;        // 开放时间，如"09:00-22:00"
    private BigDecimal ticketPrice; // 门票价格
    private String coverImage;      // 景点封面图URL
    private Double rating;          // 景点评分
    private LocalDateTime createTime;
}