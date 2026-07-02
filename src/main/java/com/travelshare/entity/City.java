package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_city")
public class City {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;           // 城市名称，如"广州"
    private String province;       // 所属省份，如"广东"
    private String description;    // 城市简介
    private String coverImage;     // 城市封面图URL
    private LocalDateTime createTime;
}