package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("tb_bill_record")
public class BillRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    private Long payerId;

    private String description;

    private Double amount;

    private LocalDate billDate;

    private String images;

    private LocalDateTime createTime;
}
