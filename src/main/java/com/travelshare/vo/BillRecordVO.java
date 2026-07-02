package com.travelshare.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillRecordVO {
    private Long id;
    private Long roomId;
    private Long payerId;
    private String description;
    private Double amount;
    private LocalDate billDate;
    private String images;
    private LocalDateTime createTime;
    private List<Long> splitUserIds;  // 分摊人员ID列表
}
