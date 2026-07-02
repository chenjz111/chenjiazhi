package com.travelshare.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSummary {
    private Long userId;
    private String username;
    private Double paid;       // 已支付金额
    private Double shouldPay;  // 应支付金额
    private Double balance;    // 余额(正=应收,负=应付)
}
