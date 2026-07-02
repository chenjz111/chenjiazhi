package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_bill_split")
public class BillSplit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long billId;

    private Long userId;
}
