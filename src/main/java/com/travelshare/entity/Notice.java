package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_notice")
public class Notice {

    private Long id;

    private String city;

    private String title;

    private String content;

    private Long publisherId;

    private String status;

    private LocalDateTime createTime;

}