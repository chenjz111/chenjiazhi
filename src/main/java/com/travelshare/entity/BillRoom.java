package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_bill_room")
public class BillRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roomNumber;

    private String name;

    private Long creatorId;

    private String status;

    private LocalDateTime createTime;
}
