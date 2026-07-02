package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("tb_partner")
public class Partner {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String startCity;

    private String targetCity;

    private LocalDate departureDate;

    private Integer requiredPeople;

    private Integer currentPeople;


    private String description;

    private String images;          // 图片URL(多张逗号分隔)

    private Long publisherId;

    private String status;

    private LocalDateTime createTime;
}