package com.travelshare.vo;

import lombok.Data;
import java.util.List;

@Data
public class SettleVO {
    private Double totalAmount;                    // 总消费金额
    private List<MemberSummary> memberSummaries;   // 每位成员汇总
    private List<TransferDetail> transfers;         // 转账明细
}
