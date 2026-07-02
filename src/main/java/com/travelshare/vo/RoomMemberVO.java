package com.travelshare.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoomMemberVO {
    private Long id;
    private Long roomId;
    private Long userId;
    private String username;   // 用户名
    private String role;
    private String status;
    private LocalDateTime joinTime;
}
