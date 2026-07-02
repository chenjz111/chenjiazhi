package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_city_notice")
public class CityNotice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String city;

    private String title;

    private String content;

    private Long publisherId;

    private String status;

    private LocalDateTime createTime;
}