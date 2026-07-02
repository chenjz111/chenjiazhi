package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_route")
public class Route {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String province;

    private String startCity;

    private String middleCity;

    private String endCity;

    private String transportType;

    private String duration;

    private BigDecimal price;

    private String description;

    private Long publisherId;

    private String status;

    private LocalDateTime createTime;
}