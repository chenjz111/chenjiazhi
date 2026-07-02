package com.travelshare.dto;

import lombok.Data;

@Data
public class CreateRoomDTO {
    private String name;
    private Long creatorId;
}
