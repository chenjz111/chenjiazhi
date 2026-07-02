package com.travelshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BillRecordDTO {

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "付款人ID不能为空")
    private Long payerId;

    @NotBlank(message = "消费描述不能为空")
    private String description;

    @NotNull(message = "金额不能为空")
    private Double amount;

    @NotNull(message = "消费日期不能为空")
    private LocalDate billDate;

    private String images;

    @NotNull(message = "分摊人员不能为空")
    private List<Long> splitUserIds;
}
