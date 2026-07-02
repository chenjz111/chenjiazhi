package com.travelshare.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDetail {
    private Long fromUserId;
    private String fromUsername;
    private Long toUserId;
    private String toUsername;
    private Double amount;
}
