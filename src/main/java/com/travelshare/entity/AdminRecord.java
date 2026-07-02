package com.travelshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审核记录表 - 记录攻略/动态/路线的审核信息
 */
@Data
@TableName("tb_admin_record")
public class AdminRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String targetType;     // 审核对象类型：strategy/travel_notice/route/partner
    private Long targetId;         // 审核对象ID
    private Long adminId;          // 审核人ID（管理员）
    private String result;         // 审核结果：APPROVED/REJECTED
    private String reason;         // 审核原因/备注
    private LocalDateTime createTime;
}