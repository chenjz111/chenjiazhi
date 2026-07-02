package com.travelshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveDTO {
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @NotBlank(message = "目标类型不能为空")
    private String targetType;  // strategy / travel_notice / route / partner

    @NotBlank(message = "审核结果不能为空")
    private String result;     // APPROVED / REJECTED

    private String reason;     // 审核原因
    private Long adminId;
}