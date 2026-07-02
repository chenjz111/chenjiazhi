package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_strategy")
public class Strategy {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String city;

    private Long authorId;

    private String images;          // 图片URL(多张逗号分隔)

    private String status;

    private LocalDateTime createTime;
}